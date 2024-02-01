package com.xiilab.modulek8s.workload.dto.response;

import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkloadResDTO {

	@Getter
	@Builder
	public static class UsingDatasetDTO{
		private String workloadName;
		private WorkloadResourceType resourceType;
		private String creator;
		private String createdAt;
	}
}
