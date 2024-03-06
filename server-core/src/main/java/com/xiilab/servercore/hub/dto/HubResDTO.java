package com.xiilab.servercore.hub.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
import com.xiilab.modulek8sdb.image.entity.HubImageEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubResDTO {
	@Getter
	@Builder
	public static class FindHub {
		private long id;
		private String title;
		private String description;
		private String thumbnailSavePath;
		private String[] types;
		private String regUserName;
		private String regDate;
		private Map<String, String> envs;
		private Map<String, Integer> ports;
		private String sourceCodeUrl;
		private String sourceCodeBranch;
		private String sourceCodeMountPath;
		private String datasetMountPath;
		private String modelMountPath;
		private String command;
		private HubResDTO.HubImage hubImage;

		public static FindHub from(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return FindHub.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.description(hubEntity.getDescription())
					.thumbnailSavePath(hubEntity.getThumbnailSavePath())
					.types(typesMap.getOrDefault(hubEntity.getHubId(), new HashSet<>()).toArray(String[]::new))
					.hubImage(new HubImage(hubEntity.getHubImageEntity()))
					.sourceCodeUrl(hubEntity.getSourceCodeUrl())
					.sourceCodeBranch(hubEntity.getSourceCodeBranch())
					.sourceCodeMountPath(hubEntity.getDatasetMountPath())
					.datasetMountPath(hubEntity.getDatasetMountPath())
					.modelMountPath(hubEntity.getModelMountPath())
					.envs(objectMapper.readValue(hubEntity.getEnvs(), new TypeReference<Map<String, String>>() {
					}))
					.ports(objectMapper.readValue(hubEntity.getPorts(), new TypeReference<Map<String, Integer>>() {
					}))
					.command(hubEntity.getCommand())
					.regUserName(hubEntity.getRegUser().getRegUserName())
					.regDate(hubEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.build();
			} catch (JsonProcessingException e) {
				throw new RuntimeException("JSON String 타입을 변환하는데 실패했습니다.");
			}
		}
	}

	@Getter
	@Builder
	public static class FindHubs {
		private List<HubResDTO.FindHub> hubsDto;
		private long totalCount;

		public static FindHubs from(List<HubEntity> hubEntities, Long totalCount, Map<Long, Set<String>> typesMap) {
			return FindHubs.builder()
				.hubsDto(hubEntities.stream().map(hubEntity -> FindHub.from(hubEntity, typesMap)).toList())
				.totalCount(totalCount)
				.build();
		}
	}

	@Getter
	public static class HubImage {
		private Long id;
		private ImageType imageType;
		private LocalDateTime regDate;
		private LocalDateTime modDate;
		private String regUserId;
		private String regUserName;
		private String regUserRealName;
		private DeleteYN deleteYN;
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private WorkloadType workloadType;

		public HubImage(HubImageEntity hubImageEntity) {
			this.id = hubImageEntity.getId();
			this.imageType = hubImageEntity.getImageType();
			this.regDate = hubImageEntity.getRegDate();
			this.modDate = hubImageEntity.getModDate();
			this.regUserId = hubImageEntity.getRegUser().getRegUserId();
			this.regUserName = hubImageEntity.getRegUser().getRegUserName();
			this.regUserRealName = hubImageEntity.getRegUser().getRegUserRealName();
			this.deleteYN = hubImageEntity.getDeleteYN();
			this.imageName = hubImageEntity.getImageName();
			this.repositoryAuthType = hubImageEntity.getRepositoryAuthType();
			this.workloadType = hubImageEntity.getWorkloadType();
		}
	}
}
