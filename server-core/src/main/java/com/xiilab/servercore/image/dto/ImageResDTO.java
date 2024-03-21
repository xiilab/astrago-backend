package com.xiilab.servercore.image.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.sql.Delete;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.HubErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ImageErrorCode;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResDTO {
	@Getter
	@SuperBuilder
	public static class FindImage extends ResDTO {
		private Long id;
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private ImageType imageType;
		private WorkloadType workloadType;
		private DeleteYN deleteYN;
		// 빌트인 이미지 필드
		private String title;
		private String description;
		private String thumbnailSavePath;
		private String thumbnailSaveFileName;
		private boolean recommendStatus;
		private boolean availableStatus;
		private Map<String, Integer> ports;
		private String command;

		public static FindImage from(ImageEntity imageEntity) {
			if (imageEntity.isBuiltInImage()) {
				BuiltInImageEntity builtInImageEntity = (BuiltInImageEntity)imageEntity;
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					return FindImage.builder()
						.id(imageEntity.getId())
						.imageName(imageEntity.getImageName())
						.repositoryAuthType(imageEntity.getRepositoryAuthType())
						.imageType(imageEntity.getImageType())
						.workloadType(imageEntity.getWorkloadType())
						.deleteYN(imageEntity.getDeleteYN())
						.title(builtInImageEntity.getTitle())
						.description(builtInImageEntity.getDescription())
						.thumbnailSavePath(builtInImageEntity.getThumbnailSavePath())
						.thumbnailSaveFileName(builtInImageEntity.getThumbnailSaveFileName())
						.ports(StringUtils.hasText(builtInImageEntity.getPorts()) ?
							objectMapper.readValue(builtInImageEntity.getPorts(),
								new TypeReference<Map<String, Integer>>() {
								}) : null)
						.command(builtInImageEntity.getCommand())
						.recommendStatus(builtInImageEntity.getRecommendStatus())
						.availableStatus(builtInImageEntity.getAvailableStatus())
						.regUserId(imageEntity.getRegUser().getRegUserId())
						.regUserName(imageEntity.getRegUser().getRegUserName())
						.regDate(imageEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
						.regUserName(imageEntity.getRegUser().getRegUserName())
						.regUserRealName(imageEntity.getRegUser().getRegUserRealName())
						.build();
				} catch (JsonProcessingException e) {
					throw new RestApiException(ImageErrorCode.NOT_FOUND_IMAGE_PORT);
				}
			} else {
				return FindImage.builder()
					.id(imageEntity.getId())
					.imageName(imageEntity.getImageName())
					.repositoryAuthType(imageEntity.getRepositoryAuthType())
					.imageType(imageEntity.getImageType())
					.workloadType(imageEntity.getWorkloadType())
					.deleteYN(imageEntity.getDeleteYN())
					.regUserId(imageEntity.getRegUser().getRegUserId())
					.regUserName(imageEntity.getRegUser().getRegUserName())
					.regDate(imageEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.regUserName(imageEntity.getRegUser().getRegUserName())
					.regUserRealName(imageEntity.getRegUser().getRegUserRealName())
					.build();
			}
		}
	}

	@Getter
	@Builder
	public static class FindImages {
		private List<ImageResDTO.FindImage> findImages;
		private long totalCount;

		public static FindImages from(List<ImageEntity> imageEntities, Long totalCount) {
			return FindImages.builder()
				.findImages(imageEntities.stream().map(FindImage::from).filter(Objects::nonNull).toList())
				.totalCount(totalCount)
				.build();
		}
	}
}
