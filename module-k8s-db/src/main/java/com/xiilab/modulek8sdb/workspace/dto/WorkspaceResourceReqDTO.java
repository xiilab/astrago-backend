package com.xiilab.modulek8sdb.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResourceReqDTO {
	private String workspace;
	private String requestReason;
	private int cpuReq;
	private int gpuReq;
	private int memReq;
}
