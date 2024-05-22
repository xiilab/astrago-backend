package com.xiilab.modulek8s.workload.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;

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
	public static class UsingDatasetDTO {
		private String workloadName;
		private WorkloadResourceType resourceType;
		private String creator;
		private String creatorName;
		private String createdAt;
		WorkloadStatus status;

		public void setStatus(WorkloadStatus workloadStatus) {
			this.status = workloadStatus;
		}

		public void setResourceType(WorkloadResourceType resourceType) {
			this.resourceType = resourceType;
		}
	}

	@Getter
	@Builder
	public static class UsingModelDTO {
		private String workloadName;
		private WorkloadResourceType resourceType;
		private String creator;
		private String createdAt;
		WorkloadStatus status;

		public void setStatus(WorkloadStatus workloadStatus) {
			this.status = workloadStatus;
		}

		public void setResourceType(WorkloadResourceType resourceType) {
			this.resourceType = resourceType;
		}
	}

	@Getter
	@Builder
	public static class WorkloadReportDTO {
		private String userName;
		private String userId;
		private String userEmail;
		private String group;
		private String workspaceName;
		private String workloadName;
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		public void setGroup(String group) {
			this.group = group;
		}
	}

}
