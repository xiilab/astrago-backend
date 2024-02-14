package com.xiilab.modulek8sdb.workspace.dto;

import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkspaceResourceQuotaState {
	private int totalCPU;
	private int totalMEM;
	private Integer totalGPU;
	private int currentCPU;
	private int currentMEM;
	private Integer currentGPU;

	public WorkspaceResourceQuotaState(ClusterResourceDTO clusterResource, ResourceQuotaResDTO workspaceResourceQuota) {
		this.totalCPU = clusterResource.getCpu();
		this.totalMEM = clusterResource.getMem();
		this.totalGPU = clusterResource.getGpu();
		this.currentCPU = workspaceResourceQuota.getReqCPU();
		this.currentMEM = workspaceResourceQuota.getReqMEM();
		this.currentGPU = workspaceResourceQuota.getReqGPU();
	}
}
