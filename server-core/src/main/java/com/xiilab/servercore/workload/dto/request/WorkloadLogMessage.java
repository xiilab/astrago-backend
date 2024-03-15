package com.xiilab.servercore.workload.dto.request;

import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadLogMessage {
	private WorkloadType workloadType;
	private String workspaceName;
	private String workloadName;
}
