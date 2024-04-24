package com.xiilab.modulek8sdb.hub.entity;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.image.entity.HubImageEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_HUB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HUB_ID")
	private Long hubId;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "THUMBNAIL_URL_GITHUB")
	private String thumbnailUrlGitHub;

	@Column(name = "THUMBNAIL_URL_GITLAB")
	private String thumbnailUrlGitLab;
	@Column(name = "README_URL_GITHUB")
	private String readmeUrlGitHub;
	@Column(name = "README_URL_GITLAB")
	private String readmeUrlGitLab;

	@Column(name = "SOURCE_CODE_URL_GITHUB")
	private String sourceCodeUrlGitHub;

	@Column(name = "SOURCE_CODE_URL_GITLAB")
	private String sourceCodeUrlGitLab;

	@Column(name = "SOURCE_CODE_BRANCH")
	private String sourceCodeBranch;

	@Column(name = "SOURCE_CODE_MOUNT_PATH")
	private String sourceCodeMountPath;

	@Column(name = "DATASET_MOUNT_PATH")
	private String datasetMountPath;

	@Column(name = "MODEL_MOUNT_PATH")
	private String modelMountPath;

	@Column(name = "ENVS", length = 1000)
	private String envs;

	@Column(name = "PARAMETER", length = 1000)
	private String parameter;

	@Column(name = "PORTS", length = 1000)
	private String ports;

	@Column(name = "COMMAND")
	private String command;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "IMAGE_ID")
	private HubImageEntity hubImageEntity;

	@Enumerated(EnumType.STRING)
	@Column(name = "WORKLOAD_TYPE")
	private WorkloadType workloadType;

	@Builder(builderMethodName = "saveBuilder", builderClassName = "saveBuilder")
	public HubEntity(String title, String description, String thumbnailUrlGitHub,
		String thumbnailUrlGitLab, String readmeUrlGitHub, String readmeUrlGitLab, String sourceCodeUrlGitHub, String sourceCodeUrlGitLab, String sourceCodeBranch, String sourceCodeMountPath,
		String datasetMountPath, String modelMountPath, String envs, String command, String parameter,
		HubImageEntity hubImageEntity, WorkloadType workloadType) {
		this.title = title;
		this.description = description;
		this.thumbnailUrlGitHub = thumbnailUrlGitHub;
		this.thumbnailUrlGitLab = thumbnailUrlGitLab;
		this.readmeUrlGitHub = readmeUrlGitHub;
		this.readmeUrlGitLab = readmeUrlGitLab;
		this.sourceCodeUrlGitHub = sourceCodeUrlGitHub;
		this.sourceCodeUrlGitLab = sourceCodeUrlGitLab;
		this.sourceCodeBranch = sourceCodeBranch;
		this.sourceCodeMountPath = sourceCodeMountPath;
		this.datasetMountPath = datasetMountPath;
		this.modelMountPath = modelMountPath;
		this.envs = envs;
		this.command = command;
		this.parameter = parameter;
		this.hubImageEntity = hubImageEntity;
		this.workloadType = workloadType;
		// 연관관계 편의 메서드
		this.hubImageEntity.getHubEntities().add(this);
	}
}
