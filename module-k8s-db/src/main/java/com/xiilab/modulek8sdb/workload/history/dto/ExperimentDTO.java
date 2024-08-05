package com.xiilab.modulek8sdb.workload.history.dto;

import com.xiilab.modulecommon.enums.WorkloadStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentDTO {
	private String workloadName;
	private String workloadResourceName;
	private String workspaceName;
	private String uuid;
	private WorkloadStatus status;
	private boolean isView;
}
