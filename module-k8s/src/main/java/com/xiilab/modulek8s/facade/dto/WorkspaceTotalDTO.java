package com.xiilab.modulek8s.facade.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
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
	private String creatorId;
	private String creatorName;
	private LocalDateTime createAt;
	private List<ResMIGDTO> migInfo;
	private int migTotalCount;
	private List<ResMPSDTO> mpsInfo;
	private int mpsTotalCount;


	public WorkspaceTotalDTO(WorkspaceDTO.ResponseDTO workspaceDTO, ResourceQuotaResDTO resourceQuotaResDTO,
		List<AbstractModuleWorkloadResDTO> workloadList) {
		this.uid = workspaceDTO.getId();
		this.name = workspaceDTO.getName();
		this.resourceName = workspaceDTO.getResourceName();
		this.description = workspaceDTO.getDescription();
		this.limitCPU = resourceQuotaResDTO.getLimitCPU();
		this.limitMEM = resourceQuotaResDTO.getLimitMEM();
		this.limitGPU = resourceQuotaResDTO.getLimitGPU();
		this.age = workspaceDTO.getCreatedAt() != null ? new AgeDTO(workspaceDTO.getCreatedAt()) : null;
		this.runningCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.RUNNING);
		this.errCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.ERROR);
		this.pendingCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.PENDING);
		this.endCnt = getWorkloadCountByStatus(workloadList, WorkloadStatus.END);
		this.creatorId = workspaceDTO.getCreatorId();
		this.creatorName = workspaceDTO.getCreatorUserName();
		this.createAt = workspaceDTO.getCreatedAt();
	}

	private int getWorkloadCountByStatus(List<AbstractModuleWorkloadResDTO> workloadList, WorkloadStatus status) {
		return (int)workloadList.stream().filter(workload -> workload.getStatus() == status).count();
	}

	public void addEndStatusWorkloadCnt(int endCnt) {
		this.endCnt += endCnt;
	}
	public void addMigInfo(List<ResMIGDTO> migInfo){
		this.migInfo = migInfo;
	}
	public void addMpsInfo(List<ResMPSDTO> mpsInfo){
		this.mpsInfo = mpsInfo;
	}
	public void setMigTotalCount(int migTotalCount){
		this.migTotalCount = migTotalCount;
	}
	public void setMpsTotalCount(int mpsTotalCount){
		this.mpsTotalCount = mpsTotalCount;
	}
}
