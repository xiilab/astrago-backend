package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkloadResDTO {

	@Builder
	@Getter
	public static class PageUsingDatasetDTO {
		private List<UsingDatasetDTO> usingWorkloads;
		private long totalCount;
	}
	@Builder
	@Getter
	public static class PageUsingModelDTO {
		private List<UsingModelDTO> usingWorkloads;
		private long totalCount;
	}

	@Getter
	@Builder
	public static class UsingDatasetDTO{
		private String workloadName;
		private WorkloadResourceType resourceType;
		private String creator;
		private String creatorName;
		private String createdAt;
		WorkloadStatus status;

		public void setStatus(WorkloadStatus workloadStatus){
			this.status = workloadStatus;
		}
		public void setResourceType(WorkloadResourceType resourceType){
			this.resourceType = resourceType;
		}
	}
	@Getter
	@Builder
	public static class UsingModelDTO{
		private String workloadName;
		private WorkloadResourceType resourceType;
		private String creator;
		private String createdAt;
		WorkloadStatus status;

		public void setStatus(WorkloadStatus workloadStatus){
			this.status = workloadStatus;
		}
		public void setResourceType(WorkloadResourceType resourceType){
			this.resourceType = resourceType;
		}
	}
}
