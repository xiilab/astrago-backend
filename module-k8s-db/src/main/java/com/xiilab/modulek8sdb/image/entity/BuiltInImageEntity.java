package com.xiilab.modulek8sdb.image.entity;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.enums.ImageType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Builder
	public BuiltInImageEntity(String imageName, RepositoryAuthType repositoryAuthType, ImageType imageType,
		WorkloadType workloadType, String title, String description,
		String thumbnailSavePath, String thumbnailSaveFileName) {
		super(imageName, repositoryAuthType, imageType, workloadType);
		this.title = title;
		this.description = description;
		this.thumbnailSavePath = thumbnailSavePath;
		this.thumbnailSaveFileName = thumbnailSaveFileName;
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
}
