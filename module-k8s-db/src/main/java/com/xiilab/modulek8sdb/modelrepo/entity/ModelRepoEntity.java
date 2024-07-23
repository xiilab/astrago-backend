package com.xiilab.modulek8sdb.modelrepo.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MODEL_REPO")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ModelRepoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_REPO_ID")
	private long id;
	@Column(name = "MODEL_REPO_NAME")
	private String modelName;
	@Column(name = "MODEL_REPO_DESCRIPTION")
	private String description;
	@Column(name = "MODEL_REPO_REAL_NAME")
	private String modelRepoRealName;
	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "MODEL_REPO_SIZE")
	private Long modelSize;
	@Column(name = "MODEL_REPO_PATH")
	private String modelPath;
	@OneToMany(mappedBy = "modelRepoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelLabelEntity> modelLabelEntityList = new ArrayList<>();
	@OneToMany(mappedBy = "modelRepoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelVersionEntity> modelVersionList = new ArrayList<>();
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;

	public void addModelLabelEntity(List<LabelEntity> labelEntityList) {
		List<ModelLabelEntity> modelLabelEntities = labelEntityList.stream()
			.map(labelEntity -> ModelLabelEntity.builder().labelEntity(labelEntity).modelRepoEntity(this).build())
			.toList();

		this.modelLabelEntityList = modelLabelEntities;
	}

	public void addModelVersionEntity(String modelFilePath, String labelFilePath) {
		this.modelVersionList = List.of(ModelVersionEntity.builder().version("v1").modelRepoEntity(this)
			.modelFileName(modelFilePath).labelFileName(labelFilePath).build());
	}

	public void modifyModelRepo(String modelName, String description) {
		this.modelName = modelName;
		this.description = description;
	}

	public void modifyModelLabel(List<LabelEntity> labelEntityList) {
		this.getModelLabelEntityList().clear();
		List<ModelLabelEntity> modelLabelEntities = labelEntityList.stream()
			.map(labelEntity -> ModelLabelEntity.builder().labelEntity(labelEntity).modelRepoEntity(this).build())
			.toList();
		this.getModelLabelEntityList().addAll(modelLabelEntities);
	}

	public void setModelSize(Long modelSize) {
		this.modelSize = modelSize;
	}

	public void setModelPath(String datasetPath) {
		this.modelPath = datasetPath;
	}

	public void updateModelRepoVersion(long versionInfo) {
		this.getModelVersionList()
			.add(ModelVersionEntity.builder()
				.version("v" + versionInfo)
				.modelRepoEntity(this)
				.build());
	}

}
