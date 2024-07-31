package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;
import java.util.Set;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workload.enums.WorkloadResourceType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WorkloadResDTO {

	@Builder
	@Getter
	public static class PageUsingVolumeDTO {
		private List<UsingWorkloadDTO> usingWorkloads;
		private long totalCount;
	}

	@Builder
	@Getter
	public static class PageUsingDatasetDTO {
		private List<UsingWorkloadDTO> usingWorkloads;
		private long totalCount;
	}

	@Builder
	@Getter
	public static class PageUsingModelDTO {
		private List<UsingWorkloadDTO> usingWorkloads;
		private long totalCount;
	}

	@Getter
	@Builder
	public static class UsingWorkloadDTO {
		private String workloadName;
		private String resourceName;
		private String workspaceResourceName;
		private WorkloadType resourceType;
		private boolean isAccessible;
		private String creator;
		private String creatorName;
		private String createdAt;
		WorkloadStatus status;

		public void setStatus(WorkloadStatus workloadStatus) {
			this.status = workloadStatus;
		}

		public void updateIsAccessible(String creator, Set<String> ownerWorkspace) {
			if (this.creator.equals(creator) || ownerWorkspace.contains(this.workspaceResourceName)) {
				this.isAccessible = true;
			}
		}

		public void setResourceType(WorkloadResourceType resourceType) {
			switch (resourceType) {
				case JOB -> this.resourceType = WorkloadType.BATCH;
				case DEPLOYMENT -> this.resourceType = WorkloadType.INTERACTIVE;
				case DISTRIBUTED -> this.resourceType = WorkloadType.DISTRIBUTED;
			}
		}
	}
}
