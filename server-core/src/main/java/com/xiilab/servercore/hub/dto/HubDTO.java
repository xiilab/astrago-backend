package com.xiilab.servercore.hub.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.servercore.hub.entity.HubEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@SuperBuilder
@Slf4j
@NoArgsConstructor
public class HubDTO {
	@Getter
	@SuperBuilder
	public static class Response {
		private long id;
		private String title;
		private String description;
		private String thumbnailSavePath;
		private String image;
		private String[] types;
		private String regUserName;
		private String regDate;

		public static Response hubEntityToResponseDto(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
			return Response.builder()
				.id(hubEntity.getHubId())
				.title(hubEntity.getTitle())
				.description(hubEntity.getDescription())
				.thumbnailSavePath(hubEntity.getThumbnailSavePath())
				.image(hubEntity.getImage())
				.types(Optional.ofNullable(typesMap.get(hubEntity.getHubId()))
					.map(list -> list.toArray(String[]::new))
					.orElseGet(() -> new String[0]))
				.regUserName(hubEntity.getRegUser().getRegUserName())
				.regDate(hubEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
				.build();
		}

		@Getter
		@SuperBuilder
		public static class HubDetailDto extends Response {
			private Map<String, String> envs;
			private Map<String, Integer> ports;
			private String sourceCodeUrl;
			private String sourceCodeBranch;
			private String sourceCodeMountPath;
			private String datasetMountPath;
			private String modelMountPath;
			private String command;

			public static HubDetailDto hubEntityToHubDetailDto(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					return HubDetailDto.builder()
						.id(hubEntity.getHubId())
						.title(hubEntity.getTitle())
						.description(hubEntity.getDescription())
						.thumbnailSavePath(hubEntity.getThumbnailSavePath())
						.image(hubEntity.getImage())
						.types(typesMap.get(hubEntity.getHubId()).toArray(String[]::new))
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
		@SuperBuilder
		public static class HubsDto {
			private List<HubDTO.Response> hubsDto;
			private long totalCount;

			public static HubsDto hubEntitiesToDto(List<HubEntity> hubEntities, Long totalCount,
				Map<Long, Set<String>> typesMap) {
				return HubsDto.builder()
					.hubsDto(
						hubEntities.stream().map(hubEntity -> hubEntityToResponseDto(hubEntity, typesMap)).toList())
					.totalCount(totalCount)
					.build();
			}

		}

	}
}
