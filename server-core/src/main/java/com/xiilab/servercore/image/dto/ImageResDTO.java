package com.xiilab.servercore.image.dto;

import java.util.List;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageResDTO {
	@Builder
	public static class FindImage{
		private Long id;
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private ImageType imageType;
		private WorkloadType workloadType;
		// 빌트인 이미지 필드
		private String title;
		private String description;
		private String thumbnailSavePath;
		private String thumbnailSaveFileName;

		public static FindImage from(ImageEntity imageEntity) {
			if (imageEntity.isBuiltInImage()) {
				return FindImage.builder()
					.id(imageEntity.getId())
					.imageName(imageEntity.getImageName())
					.repositoryAuthType(imageEntity.getRepositoryAuthType())
					.imageType(imageEntity.getImageType())
					.workloadType(imageEntity.getWorkloadType())
					.title(((BuiltInImageEntity)imageEntity).getTitle())
					.description(((BuiltInImageEntity)imageEntity).getDescription())
					.thumbnailSavePath(((BuiltInImageEntity)imageEntity).getThumbnailSavePath())
					.thumbnailSaveFileName(((BuiltInImageEntity)imageEntity).getThumbnailSaveFileName())
					.build();
			} else if (imageEntity.isCustomImage()) {

			} else if (imageEntity.isHubImage()) {

			}

			return null;
		}
	}

	@Getter
	@Builder
	public static class FindImages {
		private List<FindImage> findImages;
		private long totalCount;

		public static FindImages from(List<ImageEntity> imageEntities, Long totalCount) {
			return FindImages.builder()
				.findImages(imageEntities.stream().map(FindImage::from).toList())
				.totalCount(totalCount)
				.build();
		}
	}
}
