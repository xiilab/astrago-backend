package com.xiilab.servercore.workspace.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8sdb.workspace.enums.ResourceQuotaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceQuotaFormDTO {
	private Long id;
	private String workspaceName;
	private String workspaceResourceName;
	private String requestReason;
	private String rejectReason;
	private ResourceQuotaStatus status;
	private LocalDateTime modDate;
	private LocalDateTime regDate;
	private int cpuReq;
	private int gpuReq;
	private int memReq;
	private String requester;
}
