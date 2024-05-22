package com.xiilab.servercore.image.dto;


import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
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
		private FrameWorkType frameWorkType;
		private String cudaVersion;
		private String frameworkVersion;
		private Long credentialId;

		@Builder(builderClassName = "saveCustomImageBuilder", builderMethodName = "saveCustomImageBuilder")
		public SaveImage(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
			WorkloadType workloadType, Long credentialId) {
			this.imageName = imageName;
			this.repositoryAuthType = repositoryAuthType;
			this.imageType = imageType;
			this.workloadType = workloadType;
			this.credentialId = credentialId;
		}

		@Builder(builderClassName = "saveBuiltInImageBuilder", builderMethodName = "saveBuiltInImageBuilder")
		public SaveImage(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
			WorkloadType workloadType, String title, String description, String thumbnailSavePath, String thumbnailSaveFileName,
			FrameWorkType frameWorkType, String cudaVersion, String frameworkVersion) {
			this.imageName = imageName;
			this.repositoryAuthType = repositoryAuthType;
			this.imageType = imageType;
			this.workloadType = workloadType;
			this.title = title;
			this.description = description;
			this.thumbnailSavePath = thumbnailSavePath;
			this.thumbnailSaveFileName = thumbnailSaveFileName;
			this.frameWorkType = frameWorkType;
			this.cudaVersion = cudaVersion;
			this.frameworkVersion = frameworkVersion;
		}
	}

	@Getter
	@NoArgsConstructor
	@Setter
	public static class FindSearchCondition {
		private WorkloadType workloadType;
		private ImageType imageType;
		private boolean multiNode;
		private Integer page;
		private Integer size;

		public void setPage(Integer page) {
			this.page = !ValidUtils.isNullOrZero(page)? page - 1 : null;
		}
	}
}
