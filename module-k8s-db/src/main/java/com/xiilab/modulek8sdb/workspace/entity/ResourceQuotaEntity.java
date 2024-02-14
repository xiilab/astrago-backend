package com.xiilab.modulek8sdb.workspace.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.modulek8sdb.workspace.enums.ResourceQuotaStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_RESOURCE_QUOTA")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceQuotaEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RESOURCE_QUOTA_ID")
	private Long id;
	@Column(name = "WORKSPACE")
	private String workspace;
	@Column(name = "RESOURCE_QUOTA_REQUEST_REASON")
	private String requestReason;
	@Column(name = "RESOURCE_QUOTA_REJECT_REASON")
	private String rejectReason;
	@Enumerated(EnumType.STRING)
	@Column(name = "RESOURCE_QUOTA_STATUS")
	private ResourceQuotaStatus status;
	@Column(name = "RESOURCE_QUOTA_CPU_REQ")
	private int cpuReq;
	@Column(name = "RESOURCE_QUOTA_GPU_REQ")
	private int gpuReq;
	@Column(name = "RESOURCE_QUOTA_MEM_REQ")
	private int memReq;

	public ResourceQuotaEntity(WorkspaceResourceReqDTO workspaceResourceReqDTO) {
		this.workspace = workspaceResourceReqDTO.getWorkspace();
		this.requestReason = workspaceResourceReqDTO.getRequestReason();
		this.status = ResourceQuotaStatus.WAITING;
		this.cpuReq = workspaceResourceReqDTO.getCpuReq();
		this.memReq = workspaceResourceReqDTO.getMemReq();
		this.gpuReq = workspaceResourceReqDTO.getGpuReq();
	}

	public void approval() {
		this.status = ResourceQuotaStatus.APPROVE;
	}

	public void denied(String rejectReason) {
		this.status = ResourceQuotaStatus.REJECT;
		this.rejectReason = rejectReason;
	}
}
