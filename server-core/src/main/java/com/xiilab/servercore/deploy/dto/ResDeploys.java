package com.xiilab.servercore.deploy.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResDeploys {
	private List<ResDeploy> deploys;
	private long totalCount;

	public static ResDeploys entitiesToDtos(List<DeployEntity> deployEntities, long totalCount){
		return ResDeploys.builder()
			.totalCount(totalCount)
			.deploys(deployEntities.stream().map(ResDeploy::toDto).toList())
			.build();
	}

	@Getter
	@Builder
	public static class ResDeploy{
		private Long id;
		private String name;
		private String deployResourceName;
		private String description;
		private WorkloadStatus workloadStatus;
		private String modelName;
		private String modelVersion;
		private String creatorId;                    // 생성자 ID
		private String creatorUserName;              // 생성자 username(unique)
		private String creatorFullName;              // 생성자 fullName(unique)
		private LocalDateTime createdAt;             // 워크로드 생성일시

		public static ResDeploys.ResDeploy toDto(DeployEntity deploy){
			return ResDeploy.builder()
				.id(deploy.getId())
				.name(deploy.getName())
				.deployResourceName(deploy.getResourceName())
				.description(deploy.getDescription())
				.workloadStatus(deploy.getWorkloadStatus())
				.creatorId(deploy.getCreatorId())
				.creatorUserName(deploy.getCreatorName())
				.creatorFullName(deploy.getCreatorRealName())
				.createdAt(deploy.getCreatedAt())
				.modelName(deploy.getModelRepoEntity().getModelName())
				.modelVersion(deploy.getModelVersion())
				.build();
		}
	}
}
