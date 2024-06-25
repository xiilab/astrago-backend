package com.xiilab.modulek8sdb.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
	private String createWorkspaceCount;
	private String createWorkloadCount;
	private UsageDTO usageDTO;
	private ResourceRequestDTO resourceRequestDTO;
	private String resourceQuotaApproveCount;
	private String resourceQuotaRejectCount;
	private String createCodeCount;
	private String createDatasetCount;
	private String createCredentialCount;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class CountDTO{
		private Long count;
		private String userId;
	}
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UsageDTO{
		private Integer gpuUsage;
		private Float cpuUsage;
		private Float memUsage;
		private String userId;
	}
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResourceRequestDTO {
		private Integer gpuRequest;
		private Integer cpuRequest;
		private Integer memRequest;
		private String userId;
	}
}
