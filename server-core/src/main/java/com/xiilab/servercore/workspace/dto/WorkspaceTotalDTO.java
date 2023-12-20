package com.xiilab.servercore.workspace.dto;

import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
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
	private int limitDISK;

	public WorkspaceTotalDTO(WorkspaceDTO.ResponseDTO workspaceDTO, ResourceQuotaResDTO resourceQuotaResDTO) {
		this.uid = workspaceDTO.getId();
		this.name = workspaceDTO.getName();
		this.resourceName = workspaceDTO.getResourceName();
		this.description = workspaceDTO.getDescription();
		this.limitCPU = resourceQuotaResDTO.getLimitCPU();
		this.limitMEM = resourceQuotaResDTO.getLimitMEM();
		this.limitGPU = resourceQuotaResDTO.getLimitGPU();
		this.limitDISK = resourceQuotaResDTO.getReqDISK();
	}
}
