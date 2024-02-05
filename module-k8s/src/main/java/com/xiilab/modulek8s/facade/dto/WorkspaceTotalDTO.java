package com.xiilab.modulek8s.facade.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceTotalDTO {
	private String uid;
	private String name;
	private String resourceName;
	private String description;
	private int limitCPU;
	private int limitMEM;
	private int limitGPU;
	private AgeDTO age;
	private int runningCnt;
	private int endCnt;
	private int pendingCnt;
	private int errCnt;
	private String creator;
	private LocalDateTime createAt;

	public WorkspaceTotalDTO(WorkspaceDTO.ResponseDTO workspaceDTO, ResourceQuotaResDTO resourceQuotaResDTO) {
		this.uid = workspaceDTO.getId();
		this.name = workspaceDTO.getName();
		this.resourceName = workspaceDTO.getResourceName();
		this.description = workspaceDTO.getDescription();
		this.limitCPU = resourceQuotaResDTO.getLimitCPU();
		this.limitMEM = resourceQuotaResDTO.getLimitMEM();
		this.limitGPU = resourceQuotaResDTO.getLimitGPU();
	}

	public WorkspaceTotalDTO(WorkspaceDTO.ResponseDTO workspaceDTO, ResourceQuotaResDTO resourceQuotaResDTO, List<ModuleWorkloadResDTO> workloadList) {
		this.uid = workspaceDTO.getId();
		this.name = workspaceDTO.getName();
		this.resourceName = workspaceDTO.getResourceName();
		this.description = workspaceDTO.getDescription();
		this.limitCPU = resourceQuotaResDTO.getLimitCPU();
		this.limitMEM = resourceQuotaResDTO.getLimitMEM();
		this.limitGPU = resourceQuotaResDTO.getLimitGPU();
		this.age = DateUtils.getAge(workspaceDTO.getCreatedAt());
		this.runningCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.RUNNING);
		this.errCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.ERROR);
		this.pendingCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.PENDING);
		this.endCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.END);
		this.creator = workspaceDTO.getCreator();
		this.createAt = workspaceDTO.getCreatedAt();
	}

	private int getWorkloadCountByStatus(List<ModuleWorkloadResDTO> workloadList, WorkloadStatus status) {
		return (int) workloadList.stream().filter(workload -> workload.getStatus() == status).count();
	}

}
