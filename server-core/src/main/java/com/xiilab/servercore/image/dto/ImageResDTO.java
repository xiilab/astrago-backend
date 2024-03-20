package com.xiilab.servercore.image.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.hibernate.sql.Delete;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
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

		public static FindImage from(ImageEntity imageEntity) {
			if (imageEntity.isBuiltInImage()) {
				BuiltInImageEntity builtInImageEntity = (BuiltInImageEntity)imageEntity;
				if (builtInImageEntity.getAvailableStatus()) {
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
						.title(builtInImageEntity.getTitle())
						.description(builtInImageEntity.getDescription())
						.thumbnailSavePath(builtInImageEntity.getThumbnailSavePath())
						.thumbnailSaveFileName(builtInImageEntity.getThumbnailSaveFileName())
						.recommendStatus(builtInImageEntity.getRecommendStatus())
						.build();
				}

				return null;
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
