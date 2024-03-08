package com.xiilab.servercore.hub.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.HubErrorCode;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindHubInWorkloadResDTO extends ResDTO {
	private long id;
	private String title;
	private FindHubCommonResDTO.HubImage hubImage;
	private String sourceCodeUrl;
	private String sourceCodeBranch;
	private String sourceCodeMountPath;
	private String datasetMountPath;
	private String modelMountPath;
	private Map<String, String> envs;
	private Map<String, Integer> ports;
	private String command;

	@Getter
	@SuperBuilder
	public static class HubDetail extends FindHubInWorkloadResDTO {
		public static HubDetail of(HubEntity hubEntity) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return HubDetail.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.hubImage(new FindHubCommonResDTO.HubImage(hubEntity.getHubImageEntity()))
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
				throw new RestApiException(HubErrorCode.FAILED_ENV_MAP_TO_JSON);
			}
		}
	}

	@Getter
	@Builder
	public static class Hubs {
		private List<FindHubInWorkloadResDTO.HubDetail> hubsDto;
		private int totalCount;

		public static Hubs from(List<HubEntity> hubEntities, int totalCount) {
			return Hubs.builder()
				.hubsDto(hubEntities.stream().map(FindHubInWorkloadResDTO.HubDetail::of).toList())
				.totalCount(totalCount)
				.build();
		}
	}

}
