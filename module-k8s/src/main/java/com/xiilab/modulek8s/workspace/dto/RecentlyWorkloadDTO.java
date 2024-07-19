package com.xiilab.modulek8s.workspace.dto;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8s.common.dto.AgeDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentlyWorkloadDTO {
	private String name;
	private WorkloadStatus status;
	private AgeDTO age;
}
