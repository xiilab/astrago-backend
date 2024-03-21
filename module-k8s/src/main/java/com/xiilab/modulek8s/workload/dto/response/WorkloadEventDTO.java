package com.xiilab.modulek8s.workload.dto.response;

import com.xiilab.modulecommon.enums.K8sContainerReason;
import com.xiilab.modulek8s.common.dto.AgeDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkloadEventDTO {
	private String type;
	private K8sContainerReason reason;
	private AgeDTO age;
	private String from;
	private String message;
}
