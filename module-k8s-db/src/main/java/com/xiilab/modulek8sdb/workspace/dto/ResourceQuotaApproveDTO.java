package com.xiilab.modulek8sdb.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceQuotaApproveDTO {
	private Integer cpu;
	private Integer mem;
	private Integer gpu;
	private boolean approvalYN;
	private String rejectReason;
}
