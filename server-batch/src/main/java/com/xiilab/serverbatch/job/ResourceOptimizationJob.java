package com.xiilab.serverbatch.job;

import static com.xiilab.modulecommon.enums.MailAttribute.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.dto.SmtpDTO;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;
import com.xiilab.modulek8sdb.smtp.repository.SmtpRepository;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceOptimizationJob {
	private final PrometheusService prometheusService;
	private final SmtpRepository smtpRepository;
	private final MailService mailService;
	private final UserService userService;

	/**
	 * quartz job이 prometheus에서 데이터를 받아와 조건에 따라 최적화될 pod를 추출하는 메소드
	 *
	 * @param cpu   cpu 사용량 조건
	 * @param mem   mem 사용량 조건
	 * @param gpu   gpu 사용량 조건
	 * @param hour  사용시간 조건
	 * @param andYN cpu, mem, gpu 조건에 대한 and or 조건
	 * @return 최적화 될 pod list
	 */
	public List<ResourceOptimizationTargetDTO> getUnderResourcePodList(int cpu, int mem, int gpu, int hour,
		boolean andYN) {
		//통합을 위한 리스트 생성
		LocalDateTime now = LocalDateTime.now().minusHours(hour);
		String unixTimeStamp = String.valueOf(now.atZone(ZoneId.systemDefault()).toEpochSecond());

		//prometheus에서 기준치 미만의 리소스를 사용하고 있는 pod 조회
		List<ResponseDTO.RealTimeDTO> underResourceCPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_CPU, String.valueOf(hour), String.valueOf(cpu), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> underResourceGPUPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_GPU, String.valueOf(hour), String.valueOf(gpu), unixTimeStamp);
		List<ResponseDTO.RealTimeDTO> underResourceMEMPodList = prometheusService.getRealTimeMetric(
			Promql.RESOURCE_OPTIMIZATION_MEM, String.valueOf(hour), String.valueOf(mem), unixTimeStamp);
		log.info("under resource cpu target pod list count : {}", underResourceCPUPodList.size());
		log.info("under resource gpu target pod list count : {}", underResourceGPUPodList.size());
		log.info("under resource mem target pod list count : {}", underResourceMEMPodList.size());
		log.info("under resource condition is : {}", andYN ? "and" : "or");
		if (andYN) {
			return filterPodsByAndCondition(underResourceCPUPodList, underResourceMEMPodList,
				underResourceGPUPodList);
		} else {
			return filterPodsByOrCondition(underResourceCPUPodList, underResourceMEMPodList,
				underResourceGPUPodList);
		}
	}

	public void sendMail(List<AbstractModuleWorkloadResDTO> alarmParentList) {
		List<SmtpEntity> smtpEntities = smtpRepository.findAll();

		if (ObjectUtils.isEmpty(smtpEntities)) {
			for (AbstractModuleWorkloadResDTO alarmParent : alarmParentList) {
				// 사용자 조회
				UserDTO.UserInfo creator = userService.getUserById(alarmParent.getCreatorId());
				MailDTO mailDTO = getMailDTO(alarmParent, creator);

				for (SmtpEntity smtpEntity : smtpEntities) {
					SmtpDTO smtpDTO = SmtpDTO.builder()
						.host(smtpEntity.getHost())
						.port(smtpEntity.getPort())
						.username(smtpEntity.getUserName())
						.password(smtpEntity.getPassword())
						.build();

					boolean result = mailService.sendMail(mailDTO, smtpDTO);

					if (result) {
						break;
					}
				}
			}
		}
	}

	private MailDTO getMailDTO(AbstractModuleWorkloadResDTO aa, UserDTO.UserInfo creator) {
		MailAttribute mail = WORKLOAD_DELETE_SCHEDULED;

		// Mail Contents 작성
		List<MailDTO.Content> contents = List.of(
			MailDTO.Content.builder()
				.col1("워크스페이스 이름 : ")
				.col2(aa.getWorkspaceName())
				.build(),
			MailDTO.Content.builder()
				.col1("워크로드 이름 : ")
				.col2(aa.getName())
				.build());

		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(mail.getTitle())
			.subTitle(mail.getSubTitle())
			.contentTitle(mail.getContentTitle())
			.contents(contents)
			.receiverEmail(creator.getEmail())
			.footer(mail.getFooter())
			.build();
	}

	private List<ResourceOptimizationTargetDTO> filterPodsByAndCondition(
		List<ResponseDTO.RealTimeDTO> cpuPods,
		List<ResponseDTO.RealTimeDTO> memPods,
		List<ResponseDTO.RealTimeDTO> gpuPods) {
		//기준치 미만의 pod들을 선별하기 위해 set으로 변경
		Set<String> cpuPodName = cpuPods.stream()
			.map(ResponseDTO.RealTimeDTO::podName)
			.collect(Collectors.toSet());
		Set<String> memPodName = memPods.stream()
			.map(ResponseDTO.RealTimeDTO::podName)
			.collect(Collectors.toSet());
		Set<String> gpuPodName = gpuPods.stream()
			.map(ResponseDTO.RealTimeDTO::podName)
			.collect(Collectors.toSet());
		//기준치 미만의 pod들을 and 조건으로 선별
		Set<String> selectedPod = cpuPodName.stream()
			.filter(memPodName::contains)
			.filter(gpuPodName::contains)
			.collect(Collectors.toSet());

		log.info("resource optimization target pod list count : {}", selectedPod.size());
		return cpuPods.stream()
			.filter(pod -> selectedPod.contains(pod.podName()))
			.map(this::toResourceOptimizationTargetDTO)
			.distinct()
			.toList();
	}

	private List<ResourceOptimizationTargetDTO> filterPodsByOrCondition(
		List<ResponseDTO.RealTimeDTO> cpuPods,
		List<ResponseDTO.RealTimeDTO> memPods,
		List<ResponseDTO.RealTimeDTO> gpuPods) {
		List<ResponseDTO.RealTimeDTO> totalList = new ArrayList<>();
		totalList.addAll(cpuPods);
		totalList.addAll(memPods);
		totalList.addAll(gpuPods);
		return totalList.stream()
			.map(this::toResourceOptimizationTargetDTO)
			.distinct()
			.toList();
	}

	private ResourceOptimizationTargetDTO toResourceOptimizationTargetDTO(ResponseDTO.RealTimeDTO realTimeDTO) {
		return new ResourceOptimizationTargetDTO(realTimeDTO.nameSpace(), realTimeDTO.podName());
	}
}
