package com.xiilab.servercore.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceQuotaApproveDTO {
	private boolean approvalYN;
	private String rejectReason;
}
