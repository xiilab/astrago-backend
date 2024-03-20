package com.xiilab.modulek8sdb.image.entity;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_BUILT_IN_IMAGE")
@Getter
@DiscriminatorValue("BUILT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuiltInImageEntity extends ImageEntity {
	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	// 썸네일 저장 경로
	@Column(name = "THUMBNAIL_SAVE_PATH")
	private String thumbnailSavePath;

	// 썸네일 원본 파일명
	@Column(name = "THUMBNAIL_SAVE_FILENAME")
	private String thumbnailSaveFileName;

	@Column(name = "FRAMEWORK_TYPE")
	@Enumerated(EnumType.STRING)
	private FrameWorkType frameworkType;

	@Column(name = "CUDA_VERSION")
	private String cudaVersion;

	@Column(name = "FRAMEWORK_VERSION")
	private String frameworkVersion;

	@Transient
	private boolean availableStatus = false;

	@Transient
	private boolean recommendStatus = false;	// 라이브러리 버전 추천 STATUS


	@Builder
	public BuiltInImageEntity(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
		WorkloadType workloadType, String title, String description,
		String thumbnailSavePath, String thumbnailSaveFileName, FrameWorkType frameworkType,
		String cudaVersion, String frameworkVersion) {
		super(imageName, repositoryAuthType, imageType, workloadType);
		this.title = title;
		this.description = description;
		this.thumbnailSavePath = thumbnailSavePath;
		this.thumbnailSaveFileName = thumbnailSaveFileName;
		this.frameworkType = frameworkType;
		this.cudaVersion = cudaVersion;
		this.frameworkVersion = frameworkVersion;
	}

	@Override
	public boolean isBuiltInImage() {
		return true;
	}

	@Override
	public boolean isCustomImage() {
		return false;
	}

	@Override
	public boolean isHubImage() {
		return false;
	}

	public void setAvailableStatus(boolean availableStatus) {
		this.availableStatus = availableStatus;
	}

	public void setRecommendStatus(boolean recommendStatus) {
		this.recommendStatus = recommendStatus;
	}

	public boolean getAvailableStatus(){ return this.availableStatus; }
	public boolean getRecommendStatus() {return this.recommendStatus; }
}
