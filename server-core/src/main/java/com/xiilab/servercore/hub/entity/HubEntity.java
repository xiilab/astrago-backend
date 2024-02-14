package com.xiilab.servercore.hub.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_HUB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HubEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HUB_ID")
	private Long hubId;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "THUMBNAIL_SAVE_FILENAME")
	private String thumbnailSaveFileName;
	
	@Column(name = "THUMBNAIL_SAVE_PATH")
	private String thumbnailSavePath;

	@Column(name = "IMAGE")
	private String image;

	@Column(name = "ENVS", length = 1000)
	private String envs;

	@Column(name = "PORTS", length = 1000)
	private String ports;

	@Column(name = "SOURCE_CODE_URL")
	private String sourceCodeUrl;

	@Column(name = "SOURCE_CODE_BRANCH")
	private String sourceCodeBranch;

	@Column(name = "SOURCE_CODE_MOUNT_PATH")
	private String sourceCodeMountPath;

	@Column(name = "DATASET_MOUNT_PATH")
	private String datasetMountPath;

	@Column(name = "MODEL_MOUNT_PATH")
	private String modelMountPath;

	@Column(name = "COMMAND")
	private String command;
}
