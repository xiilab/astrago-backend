package com.xiilab.modulek8s.workload.dto.response;

import com.xiilab.modulek8s.common.dto.AgeDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkloadEventDTO {
	private String type;
	private String reason;
	private AgeDTO age;
	private String from;
	private String message;

	@Getter
	@Builder
	public static class Recently {
		private String workload;
		private String reason;
	}
}
