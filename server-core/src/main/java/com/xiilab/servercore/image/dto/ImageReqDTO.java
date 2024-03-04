package com.xiilab.servercore.image.dto;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageReqDTO {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class SaveImage {
		private String imageName;
		private RepositoryAuthType repositoryAuthType;
		private ImageType imageType;
		private WorkloadType workloadType;
		// 빌트인 이미지
		private String title;
		private String description;
		private String thumbnailSavePath;
		private String thumbnailSaveFileName;
		private Long credentialId;

		public SaveImage(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
			WorkloadType workloadType, String title, String description, String thumbnailSavePath,
			String thumbnailSaveFileName, Long credentialId) {
			this.imageName = imageName;
			this.repositoryAuthType = repositoryAuthType;
			this.imageType = imageType;
			this.workloadType = workloadType;
			this.title = title;
			this.description = description;
			this.thumbnailSavePath = thumbnailSavePath;
			this.thumbnailSaveFileName = thumbnailSaveFileName;
			this.credentialId = credentialId;
		}

		@Builder(builderClassName = "createCustomImageBuilder", builderMethodName = "createCustomImageBuilder")
		public SaveImage(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
			WorkloadType workloadType, Long credentialId) {
			this.imageName = imageName;
			this.repositoryAuthType = repositoryAuthType;
			this.imageType = imageType;
			this.workloadType = workloadType;
			this.credentialId = credentialId;
		}
	}
}
