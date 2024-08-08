package com.xiilab.servercore.hub.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.HubErrorCode;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindHubResDTO extends ResDTO {
	private long id;
	private String title;
	private String description;
	private String[] types;
	private String thumbnailFileUrl;

	public static FindHubResDTO of(HubEntity hubEntity, Map<Long, Set<String>> typesMap,
		NetworkCloseYN networkCloseYN) {
		return FindHubResDTO.builder()
			.id(hubEntity.getHubId())
			.title(hubEntity.getTitle())
			.description(hubEntity.getDescription())
			.thumbnailFileUrl("/hub/" + hubEntity.getThumbnailFileName())
			.types(typesMap.getOrDefault(hubEntity.getHubId(), new HashSet<>()).toArray(String[]::new))
			.regUserName(hubEntity.getRegUser().getRegUserName())
			.regUserId(hubEntity.getRegUser().getRegUserId())
			.regUserRealName(hubEntity.getRegUser().getRegUserRealName())
			.regDate(hubEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.build();
	}

	@Getter
	@SuperBuilder
	public static class HubDetail extends FindHubResDTO {
		private String thumbnailFileName;
		private String readmeFileName;
		private String datasetMountPath;
		private String modelMountPath;
		private FindHubCommonResDTO.HubImage hubImage;
		private Map<String, String> envs;
		private Map<String, String> parameter;
		private Map<String, Integer> ports;
		private String command;

		public static FindHubResDTO.HubDetail from(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return HubDetail.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.description(hubEntity.getDescription())
					.hubImage(new FindHubCommonResDTO.HubImage(hubEntity.getHubImageEntity()))
					.thumbnailFileName(hubEntity.getThumbnailFileName())
					.readmeFileName(hubEntity.getReadmeFileName())
					.types(typesMap.getOrDefault(hubEntity.getHubId(), new HashSet<>()).toArray(String[]::new))
					.datasetMountPath(hubEntity.getDatasetMountPath())
					.modelMountPath(hubEntity.getModelMountPath())
					.envs(hubEntity.getEnvs() != null ?
						objectMapper.readValue(hubEntity.getEnvs(), new TypeReference<>() {
						}) : null)
					.ports(hubEntity.getPorts() != null ?
						objectMapper.readValue(hubEntity.getPorts(), new TypeReference<>() {
						}) : null)
					.command(hubEntity.getCommand())
					.parameter(hubEntity.getParameter() != null ?
						objectMapper.readValue(hubEntity.getParameter(), new TypeReference<Map<String, String>>() {
						}) : null)
					.regUserName(hubEntity.getRegUser().getRegUserName())
					.regUserId(hubEntity.getRegUser().getRegUserId())
					.regUserRealName(hubEntity.getRegUser().getRegUserRealName())
					.regDate(hubEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.build();
			} catch (JsonProcessingException e) {
				throw new RestApiException(HubErrorCode.FAILED_ENV_MAP_TO_JSON);
			}
		}

		public void setHubImage(FindHubCommonResDTO.HubImage findHubCommonResDTO) {
			this.hubImage = findHubCommonResDTO;
		}

		public void setReadmeFileName(String readmeFileName) {
			this.readmeFileName = readmeFileName;
		}

		public void setThumbnailFileName(String thumbnailFileName) {
			this.thumbnailFileName = thumbnailFileName;
		}

	}

	@Getter
	@Builder
	public static class Hubs {
		private List<FindHubResDTO> hubsDto;
		private long totalCount;

		public static Hubs from(List<HubEntity> hubEntities, Long totalCount, Map<Long, Set<String>> typesMap,
			NetworkCloseYN networkCloseYN) {
			return FindHubResDTO.Hubs.builder()
				.hubsDto(hubEntities.stream()
					.map(hubEntity -> FindHubResDTO.of(hubEntity, typesMap, networkCloseYN))
					.toList())
				.totalCount(totalCount)
				.build();
		}
	}
}
