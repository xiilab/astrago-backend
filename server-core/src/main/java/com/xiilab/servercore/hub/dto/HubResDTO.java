package com.xiilab.servercore.hub.dto;

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
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class HubResDTO extends ResDTO {
	private long id;
	private String title;
	private String sourceCodeUrl;
	private String sourceCodeBranch;
	private String sourceCodeMountPath;
	private String datasetMountPath;
	private String modelMountPath;
	private String command;
	private HubResDTO.HubImage hubImage;
	private Map<String, String> envs;
	private Map<String, Integer> ports;

	@Getter
	@SuperBuilder
	public static class FindHub extends HubResDTO {
		private String description;
		private String thumbnailUrl;
		private String[] types;
		private Map<String, String> envs;
		private Map<String, Integer> ports;
		private String readmeUrl;

		public static FindHub from(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return FindHub.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.description(hubEntity.getDescription())
					.thumbnailUrl(hubEntity.getThumbnailURL())
					.readmeUrl(hubEntity.getReadmeURL())
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
					.regUserId(hubEntity.getRegUser().getRegUserId())
					.regUserRealName(hubEntity.getRegUser().getRegUserRealName())
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
	@SuperBuilder
	public static class FindHubInWorkload extends HubResDTO {
		public static FindHubInWorkload from(HubEntity hubEntity) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return HubResDTO.FindHubInWorkload.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.hubImage(new HubResDTO.HubImage(hubEntity.getHubImageEntity()))
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
					.regUserId(hubEntity.getRegUser().getRegUserId())
					.regUserRealName(hubEntity.getRegUser().getRegUserRealName())
					.regDate(hubEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.build();
			} catch (JsonProcessingException e) {
				throw new RuntimeException("JSON String 타입을 변환하는데 실패했습니다.");
			}
		}
	}

	@Getter
	@Builder
	public static class FindHubsInWorkload {
		private List<HubResDTO.FindHubInWorkload> hubsDto;
		private int totalCount;

		public static FindHubsInWorkload from(List<HubEntity> hubEntities, int totalCount) {
			return HubResDTO.FindHubsInWorkload.builder()
				.hubsDto(hubEntities.stream().map(FindHubInWorkload::from).toList())
				.totalCount(totalCount)
				.build();
		}
	}

	@Getter
	public static class HubImage extends ResDTO {
		private Long id;
		private ImageType imageType;
		private DeleteYN deleteYN;
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private WorkloadType workloadType;

		public HubImage(HubImageEntity hubImageEntity) {
			super(hubImageEntity.getRegUser().getRegUserId(), hubImageEntity.getRegUser().getRegUserName(),
				hubImageEntity.getRegUser().getRegUserRealName(), hubImageEntity.getRegDate(),
				hubImageEntity.getModDate());
			this.id = hubImageEntity.getId();
			this.imageType = hubImageEntity.getImageType();
			this.deleteYN = hubImageEntity.getDeleteYN();
			this.imageName = hubImageEntity.getImageName();
			this.repositoryAuthType = hubImageEntity.getRepositoryAuthType();
			this.workloadType = hubImageEntity.getWorkloadType();
		}
	}
}
