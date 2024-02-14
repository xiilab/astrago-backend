package com.xiilab.modulek8sdb.image.entity;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.entity.ImageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_BUILT_IN_IMAGE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BuiltInImageEntity extends ImageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private Long id;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "IMAGE")
	private String image;

	@Column(name = "THUMBNAIL_SAVE_PATH")
	private String thumbnailSavePath;

	@Column(name = "THUMBNAIL_SAVE_FILENAME")
	private String thumbnailSaveFileName;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE", insertable = false, updatable = false)
	private WorkloadType type;

}
