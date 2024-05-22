package com.xiilab.servercore.workload.service;

import static com.xiilab.modulecommon.enums.WorkloadType.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.enumeration.EntityMappingType;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.image.repository.ImageWorkloadMappingRepository;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepoCustom;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.user.service.UserFacadeService;
import com.xiilab.servercore.workload.dto.request.WorkloadHistoryReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.servercore.workload.dto.response.OverViewWorkloadResDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkloadHistoryServiceImpl implements WorkloadHistoryService {
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final WorkloadHistoryRepoCustom workloadHistoryRepoCustom;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;
	private final ImageWorkloadMappingRepository imageWorkloadMappingRepository;
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final CodeRepository codeRepository;
	private final ImageRepository imageRepository;
	private final ApplicationEventPublisher publisher;
	private final UserFacadeService userFacadeService;

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName,
		Boolean isCreatedByMe, String userId) {
		List<JobEntity> batchJobEntityList = null;
		if (ValidUtils.isNullOrFalse(isCreatedByMe)) { //overview 페이지에서 요청 시
			batchJobEntityList = workloadHistoryRepoCustom.findBatchWorkloadHistoryByCondition(
				workspaceName, searchName, StringUtils.hasText(workspaceName) ? null : userId, BATCH);
		} else {
			batchJobEntityList = workloadHistoryRepoCustom.findBatchWorkloadHistoryByCondition(
				workspaceName, searchName, userId, BATCH);
		}

		return batchJobEntityList.stream().map(job -> ModuleBatchJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.resourceName(job.getResourceName())
				.description(job.getDescription())
				.status(job.getWorkloadStatus())
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(BATCH)
				.creatorId(job.getCreatorId())
				.creatorUserName(job.getCreatorName())
				.creatorFullName(job.getCreatorRealName())
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
					job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName,
		Boolean isCreatedByMe, String userId) {
		List<JobEntity> interactiveJobList = null;
		if (ValidUtils.isNullOrFalse(isCreatedByMe)) {
			interactiveJobList = workloadHistoryRepoCustom.findBatchWorkloadHistoryByCondition(
				workspaceName, searchName, StringUtils.hasText(workspaceName) ? null : userId, INTERACTIVE);
		} else {
			interactiveJobList = workloadHistoryRepoCustom.findBatchWorkloadHistoryByCondition(
				workspaceName, searchName, userId, INTERACTIVE);
		}

		return interactiveJobList.stream().map(job -> ModuleInteractiveJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.resourceName(job.getResourceName())
				.description(job.getDescription())
				.status(job.getWorkloadStatus())
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(WorkloadType.INTERACTIVE)
				.creatorId(job.getCreatorId())
				.creatorUserName(job.getCreatorName())
				.creatorFullName(job.getCreatorRealName())
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.ide(job.getIde())
				.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public ModuleWorkloadResDTO getWorkloadHistoryById(long id) {
		JobEntity job = workloadHistoryRepo.findById(id).orElseThrow();
		if (job.getWorkloadType() == BATCH) {
			return ModuleBatchJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.resourceName(job.getResourceName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
					job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.build();
		} else {
			return ModuleInteractiveJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.resourceName(job.getResourceName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(WorkloadType.INTERACTIVE)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.build();
		}
	}

	@Override
	public FindWorkloadResDTO.WorkloadDetail getWorkloadInfoByResourceName(String workspaceName,
		String workloadResourceName, UserDTO.UserInfo userInfoDTO) {
		JobEntity jobEntity = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workspaceName, workloadResourceName)
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		// 삭제된 워크로드는 다른 에러메시지 처리
		if (jobEntity.getDeleteYN() == DeleteYN.Y) {
			throw new RestApiException(WorkloadErrorCode.DELETED_WORKLOAD_INFO);
		}
		Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);
		jobEntity.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);

		return FindWorkloadResDTO.WorkloadDetail.from(jobEntity);
	}

	@Override
	public void deleteWorkloadHistory(long id, UserDTO.UserInfo userInfoDTO) {
		JobEntity jobEntity = workloadHistoryRepo.findById(id).orElseThrow();
		// owner 권한인 워크스페이스 목록 가져옴
		List<String> loginUserOwnerWorkspaceList = userInfoDTO.getWorkspaces()
			.stream()
			.filter(workspace -> workspace.contains("/owner"))
			.map(workspace -> workspace.split("/owner")[0])
			.toList();

		String workloadName = jobEntity.getName();
		WorkspaceUserAlertEvent workspaceUserAlertEvent = null;
		// 워크로드 생성자가 삭제
		if (jobEntity.getCreatorId().equals(userInfoDTO.getId())) {

			String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_DELETE_CREATOR.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMessage(), workloadName);

			MailDTO mail = MailDTO.builder()
				.subject(String.format(MailAttribute.WORKLOAD_DELETE.getSubject(), jobEntity.getName()))
				.title(String.format(MailAttribute.WORKLOAD_DELETE.getTitle(), jobEntity.getName()))
				.subTitle(String.format(MailAttribute.WORKLOAD_DELETE.getSubTitle(),
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.footer(MailAttribute.WORKLOAD_DELETE.getFooter())
				.receiverEmail(userFacadeService.getUserInfoById(jobEntity.getCreatorId()).getEmail())
				.build();

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
				userInfoDTO.getId(), jobEntity.getCreatorId(), emailTitle, title, message,
				jobEntity.getWorkspaceResourceName(), null, mail);

		} else if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN || loginUserOwnerWorkspaceList.contains(
			jobEntity.getWorkspaceResourceName())) {    // 관리자 또는 워크스페이스 생성자가 삭제

			String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_DELETE_ADMIN.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMessage(),
				userInfoDTO.getUserFullName(), userInfoDTO.getEmail(), workloadName);
			MailDTO mailDTo = MailDTO.builder()
				.subject(String.format(MailAttribute.WORKLOAD_DELETE.getSubject(), jobEntity.getName()))
				.title(String.format(MailAttribute.WORKLOAD_DELETE.getTitle(), jobEntity.getName()))
				.subTitle(String.format(MailAttribute.WORKLOAD_DELETE.getSubTitle(),
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.footer(MailAttribute.WORKLOAD_DELETE.getFooter())
				.receiverEmail(userFacadeService.getUserInfoById(jobEntity.getCreatorId()).getEmail())
				.build();

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
				userInfoDTO.getId(), jobEntity.getCreatorId(), emailTitle, title, message,
				jobEntity.getWorkspaceResourceName(), null, mailDTo);
		} else {
			throw new IllegalArgumentException("해당 유저는 워크스페이스 삭제 권한이 없습니다.");
		}

		workloadHistoryRepo.deleteById(id);
		publisher.publishEvent(workspaceUserAlertEvent);
	}

	@Override
	public void saveWorkloadHistory(WorkloadHistoryReqDTO.CreateWorkloadHistory createWorkloadHistory) {
		// IMAGE 찾기
		long imageId = Long.parseLong(createWorkloadHistory.getImageId());
		ImageEntity imageEntity = imageRepository.findById(imageId)
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_SAVE_WORKLOAD_HISTORY));

		JobEntity jobEntity = JobEntity.jobBuilder()
			.name(createWorkloadHistory.getName())
			.description(createWorkloadHistory.getDescription())
			.resourceName(createWorkloadHistory.getResourceName())
			.workspaceName(createWorkloadHistory.getWorkspaceName())
			.workspaceResourceName(createWorkloadHistory.getWorkspaceResourceName())
			.ports(createWorkloadHistory.getPorts())
			.envs(createWorkloadHistory.getEnvs())
			.cpuReq(createWorkloadHistory.getCpuRequest())
			.memReq(createWorkloadHistory.getMemRequest())
			.gpuReq(createWorkloadHistory.getGpuRequest())
			.workloadCmd(createWorkloadHistory.getCmd())
			.createdAt(createWorkloadHistory.getCreatedAt())
			.creatorRealName(createWorkloadHistory.getCreatorRealName())
			.creatorName(createWorkloadHistory.getCreatorName())
			.creatorId(createWorkloadHistory.getCreatorId())
			.workloadType(BATCH)
			.image(imageEntity)
			.build();
		JobEntity job = workloadHistoryRepo.save(jobEntity);

		// Dataset Mapping 엔티티 추가
		saveDataMapping(getSplitIds(createWorkloadHistory.getDatasetIds()), datasetRepository::findById, job,
			EntityMappingType.DATASET, createWorkloadHistory.getDatasetInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getModelIds()), modelRepository::findById, job,
			EntityMappingType.MODEL, createWorkloadHistory.getModelInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getCodeIds()), codeRepository::findById, job,
			EntityMappingType.CODE, createWorkloadHistory.getCodesInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getImageId()), imageRepository::findById, job,
			EntityMappingType.IMAGE, null);

	}

	@Override
	public void editWorkloadHistory(WorkloadUpdateDTO workloadUpdateDTO) {
		JobEntity findWorkload = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workloadUpdateDTO.getWorkspaceResourceName(),
				workloadUpdateDTO.getWorkloadResourceName())
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		findWorkload.updateJob(workloadUpdateDTO.getName(), workloadUpdateDTO.getDescription());
		workloadHistoryRepo.save(findWorkload);

	}

	@Override
	public ModuleWorkloadResDTO findByWorkspaceAndRecently(String workspaceName, String username) {
		JobEntity job = workloadHistoryRepoCustom.findByWorkspaceNameRecently(workspaceName, username);
		if (Objects.nonNull(job)) {
			if (job.getWorkloadType() == BATCH) {
				return ModuleBatchJobResDTO.builder()
					.uid(String.valueOf(job.getId()))
					.name(job.getName())
					.resourceName(job.getResourceName())
					.description(job.getDescription())
					.status(WorkloadStatus.END)
					.workspaceName(job.getWorkspaceName())
					.workspaceResourceName(job.getWorkspaceResourceName())
					.type(BATCH)
					.createdAt(job.getCreatedAt())
					.deletedAt(job.getDeletedAt())
					.age(new AgeDTO(job.getCreatedAt()))
					.command(job.getWorkloadCMD())
					.cpuRequest(String.valueOf(job.getCpuRequest()))
					.memRequest(String.valueOf(job.getMemRequest()))
					.gpuRequest(String.valueOf(job.getGpuRequest()))
					.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
						job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					.build();
			} else {
				return ModuleInteractiveJobResDTO.builder()
					.uid(String.valueOf(job.getId()))
					.name(job.getName())
					.resourceName(job.getResourceName())
					.description(job.getDescription())
					.status(WorkloadStatus.END)
					.workspaceName(job.getWorkspaceName())
					.workspaceResourceName(job.getWorkspaceResourceName())
					.type(WorkloadType.INTERACTIVE)
					.createdAt(job.getCreatedAt())
					.deletedAt(job.getDeletedAt())
					.age(new AgeDTO(job.getCreatedAt()))
					.command(job.getWorkloadCMD())
					.cpuRequest(String.valueOf(job.getCpuRequest()))
					.memRequest(String.valueOf(job.getMemRequest()))
					.gpuRequest(String.valueOf(job.getGpuRequest()))
					.build();
			}
		} else {
			return null;
		}
	}

	@Override
	public List<JobEntity> getWorkloadByResourceName(String workspaceResourceName) {
		return workloadHistoryRepo.findByWorkspaceResourceName(workspaceResourceName);
	}

	@Override
	public void deleteWorkload(String workspaceName) {
		List<JobEntity> jobEntities = workloadHistoryRepo.findByWorkspaceResourceName(workspaceName);
		for (JobEntity jobEntity : jobEntities) {
			workloadHistoryRepo.deleteById(jobEntity.getId());
		}
	}

	@Override
	public List<JobEntity> getWorkloadByResourceNameAndStatus(String workspaceResourceName,
		WorkloadStatus workloadStatus) {
		return workloadHistoryRepo.getWorkloadByResourceNameAndStatus(workspaceResourceName, workloadStatus);
	}

	@Override
	public List<ModuleWorkloadResDTO> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList,
		WorkloadType workloadType, WorkloadSortCondition sortCondition) {
		List<JobEntity> workloads = workloadHistoryRepoCustom.getWorkloadHistoryInResourceNames(pinResourceNameList, workloadType, sortCondition);
		if(workloadType == BATCH){
			return workloads.stream().map(job -> ModuleBatchJobResDTO.builder()
					.uid(String.valueOf(job.getId()))
					.name(job.getName())
					.resourceName(job.getResourceName())
					.description(job.getDescription())
					.status(job.getWorkloadStatus())
					.workspaceName(job.getWorkspaceName())
					.workspaceResourceName(job.getWorkspaceResourceName())
					.type(workloadType)
					.creatorId(job.getCreatorId())
					.creatorUserName(job.getCreatorName())
					.creatorFullName(job.getCreatorRealName())
					.createdAt(job.getCreatedAt())
					.deletedAt(job.getDeletedAt())
					.age(new AgeDTO(job.getCreatedAt()))
					.command(job.getWorkloadCMD())
					.cpuRequest(String.valueOf(job.getCpuRequest()))
					.memRequest(String.valueOf(job.getMemRequest()))
					.gpuRequest(String.valueOf(job.getGpuRequest()))
					.startTime(job.getStartTime() != null ? job.getStartTime().format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : null)
					.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
						job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
					.build())
				.collect(Collectors.toList());
		}else{
			return workloads.stream().map(job -> ModuleInteractiveJobResDTO.builder()
					.uid(String.valueOf(job.getId()))
					.name(job.getName())
					.resourceName(job.getResourceName())
					.description(job.getDescription())
					.status(job.getWorkloadStatus())
					.workspaceName(job.getWorkspaceName())
					.workspaceResourceName(job.getWorkspaceResourceName())
					.type(workloadType)
					.creatorId(job.getCreatorId())
					.creatorUserName(job.getCreatorName())
					.creatorFullName(job.getCreatorRealName())
					.createdAt(job.getCreatedAt())
					.deletedAt(job.getDeletedAt())
					.age(new AgeDTO(job.getCreatedAt()))
					.command(job.getWorkloadCMD())
					.cpuRequest(String.valueOf(job.getCpuRequest()))
					.memRequest(String.valueOf(job.getMemRequest()))
					.gpuRequest(String.valueOf(job.getGpuRequest()))
					.startTime(job.getStartTime() != null ? job.getStartTime().format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : null)
					.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
						job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
					.build())
				.collect(Collectors.toList());
		}

	}

	@Override
	public OverViewWorkloadResDTO<ModuleWorkloadResDTO> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType, String searchName,
		Boolean isCreatedByMe, String userId, List<String> pinResourceNameList, WorkloadStatus workloadStatus,
		WorkloadSortCondition workloadSortCondition, PageRequest pageRequest) {
		//overview 페이지에서 요청 or 워크로드 전체 조회 페이지에서 내가 생성한 워크로드 체크 해제 시
		List<ModuleWorkloadResDTO> workloads;
		Page<JobEntity> workloadEntities;
		if (ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadEntities = workloadHistoryRepoCustom.getOverViewWorkloadList(
				workspaceName, workloadType, searchName, StringUtils.hasText(workspaceName) ? null : userId, pinResourceNameList, workloadSortCondition, pageRequest, workloadStatus);
		} else {
			workloadEntities = workloadHistoryRepoCustom.getOverViewWorkloadList(
				workspaceName, workloadType, searchName, userId, pinResourceNameList, workloadSortCondition, pageRequest,
				workloadStatus);
		}
		if(workloadType == BATCH){
			workloads = workloadEntities.getContent().stream().map(job ->
				ModuleBatchJobResDTO.builder()
							.uid(String.valueOf(job.getId()))
							.name(job.getName())
							.resourceName(job.getResourceName())
							.description(job.getDescription())
							.status(job.getWorkloadStatus())
							.workspaceName(job.getWorkspaceName())
							.workspaceResourceName(job.getWorkspaceResourceName())
							.type(workloadType)
							.creatorId(job.getCreatorId())
							.creatorUserName(job.getCreatorName())
							.creatorFullName(job.getCreatorRealName())
							.createdAt(job.getCreatedAt())
							.deletedAt(job.getDeletedAt())
							.age(new AgeDTO(job.getCreatedAt()))
							.command(job.getWorkloadCMD())
							.cpuRequest(String.valueOf(job.getCpuRequest()))
							.memRequest(String.valueOf(job.getMemRequest()))
							.gpuRequest(String.valueOf(job.getGpuRequest()))
					.startTime(job.getStartTime() != null ? job.getStartTime().format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : null)
					.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
						job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
							.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
							.build()
				).collect(Collectors.toList());
		}else{
			workloads = workloadEntities.getContent().stream().map(job ->
				ModuleInteractiveJobResDTO.builder()
					.uid(String.valueOf(job.getId()))
					.name(job.getName())
					.resourceName(job.getResourceName())
					.description(job.getDescription())
					.status(job.getWorkloadStatus())
					.workspaceName(job.getWorkspaceName())
					.workspaceResourceName(job.getWorkspaceResourceName())
					.type(workloadType)
					.creatorId(job.getCreatorId())
					.creatorUserName(job.getCreatorName())
					.creatorFullName(job.getCreatorRealName())
					.createdAt(job.getCreatedAt())
					.deletedAt(job.getDeletedAt())
					.age(new AgeDTO(job.getCreatedAt()))
					.command(job.getWorkloadCMD())
					.cpuRequest(String.valueOf(job.getCpuRequest()))
					.memRequest(String.valueOf(job.getMemRequest()))
					.gpuRequest(String.valueOf(job.getGpuRequest()))
					.startTime(job.getStartTime() != null ? job.getStartTime().format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) : null)
					.estimatedRemainingTime(LocalDateTime.now().plusSeconds(
						job.getRemainTime()).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
					.imageType(!ObjectUtils.isEmpty(job.getImage()) ? job.getImage().getImageType().name() : null)
					.build()
			).collect(Collectors.toList());
		}
		long totalCount = workloadEntities.getTotalElements();
		return new OverViewWorkloadResDTO<>(totalCount, workloads);
	}

	private String[] getSplitIds(String ids) {
		return ids != null ? ids.split(",") : null;
	}

	// 데이터셋 또는 모델 정보를 저장하는 메서드
	private void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, JobEntity jobEntity,
		EntityMappingType type, Map<Long, Map<String, String>> infoMap) {
		if (ids != null) {
			for (String id : ids) {
				if (StringUtils.hasText(id)) {
					Optional<?> optionalEntity = findByIdFunction.apply(Long.valueOf(id));
					optionalEntity.ifPresent(entity -> {
						if (type == EntityMappingType.DATASET) {
							Dataset dataset = (Dataset)entity;
							Map<String, String> datasetInfoMap = infoMap.get(dataset.getDatasetId());
							DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity = DatasetWorkLoadMappingEntity.builder()
								.dataset(dataset)
								.workload(jobEntity)
								.mountPath(datasetInfoMap.getOrDefault("mountPath", ""))
								.build();
							datasetWorkLoadMappingRepository.save(datasetWorkLoadMappingEntity);
						} else if (type == EntityMappingType.MODEL) {
							Model model = (Model)entity;
							Map<String, String> modelInfoMap = infoMap.get(model.getModelId());

							ModelWorkLoadMappingEntity modelWorkLoadMappingEntity = ModelWorkLoadMappingEntity.builder()
								.model(model)
								.workload(jobEntity)
								.mountPath(modelInfoMap.getOrDefault("mountPath", ""))
								.build();
							modelWorkLoadMappingRepository.save(modelWorkLoadMappingEntity);
						} else if (type == EntityMappingType.CODE) {
							CodeEntity code = (CodeEntity)entity;
							Map<String, String> codeInfoMap = infoMap.get(code.getId());

							CodeWorkLoadMappingEntity codeWorkLoadMappingEntity = CodeWorkLoadMappingEntity.builder()
								.workload(jobEntity)
								.code(code)
								.branch(codeInfoMap.getOrDefault("branch", ""))
								.mountPath(codeInfoMap.getOrDefault("mountPath", ""))
								.build();
							codeWorkLoadMappingRepository.save(codeWorkLoadMappingEntity);
						} else if (type == EntityMappingType.IMAGE) {
							ImageEntity image = (ImageEntity)entity;

							ImageWorkloadMappingEntity imageWorkloadMappingEntity = ImageWorkloadMappingEntity.builder()
								.workload(jobEntity)
								.image(image)
								.build();
							imageWorkloadMappingRepository.save(imageWorkloadMappingEntity);
						}
					});
				}
			}
		}
	}

}
