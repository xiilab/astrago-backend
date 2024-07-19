package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkspaceResourceQuotaState {
	private float totalCPU;
	private float totalMEM;
	private float totalGPU;
	private float currentCPU;
	private float currentMEM;
	private float currentGPU;

	public WorkspaceResourceQuotaState(ClusterResourceDTO clusterResource, ResourceQuotaResDTO workspaceResourceQuota) {
		this.totalCPU = clusterResource.getCpu();
		this.totalMEM = clusterResource.getMem();
		this.totalGPU = clusterResource.getGpu();
		this.currentCPU = workspaceResourceQuota.getReqCPU();
		this.currentMEM = workspaceResourceQuota.getReqMEM();
		this.currentGPU = workspaceResourceQuota.getReqGPU();
	}

	public WorkspaceResourceQuotaState(WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus) {
		this.totalCPU = workspaceResourceStatus.getResourceStatus().getCpuLimit();
		this.totalMEM = workspaceResourceStatus.getResourceStatus().getMemLimit();
		this.totalGPU = workspaceResourceStatus.getResourceStatus().getGpuLimit();
		this.currentCPU = workspaceResourceStatus.getResourceStatus().getCpuUsed();
		this.currentMEM = workspaceResourceStatus.getResourceStatus().getMemUsed();
		this.currentGPU = workspaceResourceStatus.getResourceStatus().getGpuUsed();
	}
}
