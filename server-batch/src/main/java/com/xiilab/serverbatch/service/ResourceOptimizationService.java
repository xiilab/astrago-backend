package com.xiilab.serverbatch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8sdb.report.entity.ResourceOptimizationJobEntity;
import com.xiilab.modulek8sdb.report.entity.ResourceOptimizationWorkloadEntity;
import com.xiilab.modulek8sdb.report.repository.ResourceOptimizationJobReopsitory;
import com.xiilab.serverbatch.dto.ResourceOptimizationReportDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResourceOptimizationService {
	private final ResourceOptimizationJobReopsitory resourceOptimizationJobRepository;

	@Transactional
	public void saveResourceOptimizationReport(ResourceOptimizationReportDTO reportDTO) {
		// Optimization Job 저장
		ResourceOptimizationJobEntity jobEntity = buildResourceOptimizationJobEntity(reportDTO);

		// Optimization Workload 정보 저장
		List<ResourceOptimizationWorkloadEntity> workloadEntities = buildWorkloadEntityList(
			reportDTO.getResourceOptimizationTargets(), jobEntity);

		jobEntity.addWorkloadInfo(workloadEntities);

		resourceOptimizationJobRepository.save(jobEntity);
	}

	private ResourceOptimizationJobEntity buildResourceOptimizationJobEntity(ResourceOptimizationReportDTO reportDTO) {
		return ResourceOptimizationJobEntity.builder()
			.cpuCondition(reportDTO.getCpuLimit())
			.memCondition(reportDTO.getMemLimit())
			.gpuCondition(reportDTO.getGpuLimit())
			.andYn(reportDTO.isAndYN())
			.startTime(reportDTO.getStartTime())
			.hour(reportDTO.getHour())
			.optimizationCount(reportDTO.getOptimizationResultCnt())
			.build();
	}

	private List<ResourceOptimizationWorkloadEntity> buildWorkloadEntityList(
		List<ResourceOptimizationTargetDTO> targetDTOList,
		ResourceOptimizationJobEntity jobEntity) {

		if (CollectionUtils.isEmpty(targetDTOList)) {
			return new ArrayList<>();
		}

		return targetDTOList.stream()
			.map(dto -> buildWorkloadEntity(dto, jobEntity))
			.collect(Collectors.toList());
	}

	private ResourceOptimizationWorkloadEntity buildWorkloadEntity(ResourceOptimizationTargetDTO dto,
		ResourceOptimizationJobEntity jobEntity) {
		return ResourceOptimizationWorkloadEntity.builder()
			.workloadResourceName(getWlName(dto.getPodName()))
			.workspaceResourceName(dto.getNamespace())
			.cpuUsage(dto.getCpuUsage())
			.memUsage(dto.getMemUsage())
			.gpuUsage(dto.getGpuUsage())
			.resourceOptimizationJob(jobEntity)
			.build();
	}

	private String getWlName(String podName) {
		// 마지막 '-'의 위치를 찾는다.
		int lastDashIndex = podName.lastIndexOf('-');
		if (lastDashIndex == -1) {
			return podName;  // '-'가 없으면 전체 이름을 반환
		}

		// 마지막 '-' 바로 전의 '-'의 위치를 찾는다.
		int secondLastDashIndex = podName.lastIndexOf('-', lastDashIndex - 1);
		if (secondLastDashIndex == -1) {
			return podName;  // 두 번째 '-'가 없으면 전체 이름을 반환
		}

		// 두 번째 '-'까지의 부분을 추출하여 반환한다.
		return podName.substring(0, secondLastDashIndex);
	}
}
