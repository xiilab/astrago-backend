package com.xiilab.servercore.hub.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

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
		public static HubDetail of(HubEntity hubEntity, NetworkCloseYN networkCloseYN, String privateRegistryUrl) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				FindHubCommonResDTO.HubImage hubImageDto = new FindHubCommonResDTO.HubImage(
					hubEntity.getHubImageEntity());
				// hubImageDto.setImageName(networkCloseYN == NetworkCloseYN.Y ?
				// 	privateRepositoryUrl + "/" + hubEntity.getHubImageEntity().getImageName() :
				// 	hubEntity.getHubImageEntity().getImageName());
				// String hubImageUrl = "";
				// if(networkCloseYN == NetworkCloseYN.Y){
				// 	if(isBlankSafe(privateRegistryUrl)){
				// 		hubImageUrl = hubEntity.getHubImageEntity().getImageName();
				// 	}else{
				// 		hubImageUrl = privateRegistryUrl + "/" + hubEntity.getHubImageEntity().getImageName();
				// 	}
				// }else{
				// 	hubImageUrl = hubEntity.getHubImageEntity().getImageName();
				// }
				// hubImageDto.setImageName(hubImageUrl);

				return HubDetail.builder()
					.id(hubEntity.getHubId())
					.title(hubEntity.getTitle())
					.hubImage(hubImageDto)
					// .sourceCodeUrl(networkCloseYN == NetworkCloseYN.Y ? hubEntity.getSourceCodeUrl() :
					// 	hubEntity.getSourceCodeUrl())
					.sourceCodeMountPath(hubEntity.getDatasetMountPath())
					.datasetMountPath(hubEntity.getDatasetMountPath())
					.modelMountPath(hubEntity.getModelMountPath())
					.envs(StringUtils.hasText(hubEntity.getEnvs()) ? objectMapper.readValue(hubEntity.getEnvs(),
						new TypeReference<>() {
						}) : null)
					.ports(StringUtils.hasText(hubEntity.getPorts()) ? objectMapper.readValue(hubEntity.getPorts(),
						new TypeReference<>() {
						}) : null)
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
		// null 체크와 함께 isBlank를 수행하는 메서드
		public static boolean isBlankSafe(String str) {
			return str == null || str.isBlank();
		}
	}

	@Getter
	@Builder
	public static class Hubs {
		private List<FindHubInWorkloadResDTO.HubDetail> hubsDto;
		private int totalCount;

		public static Hubs from(List<HubEntity> hubEntities, int totalCount, NetworkCloseYN networkCloseYN, String privateRegistryUrl) {
			return Hubs.builder()
				.hubsDto(hubEntities.stream()
					.map(hubEntity -> HubDetail.of(hubEntity, networkCloseYN, privateRegistryUrl))
					.toList())
				.totalCount(totalCount)
				.build();
		}
	}

}
