package com.xiilab.servercore.workload.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;
import com.xiilab.modulek8sdb.repository.WorkloadHistoryRepoCusotm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadHistoryServiceImpl implements WorkloadHistoryService {
	private final WorkloadHistoryRepoCusotm workloadHistoryRepoCusotm;

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName,
		String userId) {
		List<JobEntity> batchJobEntityList = workloadHistoryRepoCusotm.findBatchWorkloadHistoryByCondition(
			workspaceName, searchName, userId, WorkloadType.BATCH);
		return batchJobEntityList.stream().map(job -> ModuleBatchJobResDTO.builder()
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulek8s.workload.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.remainTime(0)
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName,
		String userId) {
		List<JobEntity> batchJobEntityList = workloadHistoryRepoCusotm.findBatchWorkloadHistoryByCondition(
			workspaceName, searchName, userId, WorkloadType.INTERACTIVE);
		return batchJobEntityList.stream().map(job -> ModuleInteractiveJobResDTO.builder()
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulek8s.workload.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.build())
			.collect(Collectors.toList());
	}
}
