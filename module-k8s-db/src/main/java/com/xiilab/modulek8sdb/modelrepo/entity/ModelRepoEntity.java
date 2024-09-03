package com.xiilab.modulek8sdb.modelrepo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MODEL_REPO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ModelRepoEntity extends BaseEntity {
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
	@Column(name = "MODEL_REPO_PATH")
	private String modelPath;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;
	@OneToMany(mappedBy = "modelRepoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelLabelEntity> modelLabelEntityList = new ArrayList<>();
	@OneToMany(mappedBy = "modelRepoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelVersionEntity> modelVersionList = new ArrayList<>();

	@Builder
	public ModelRepoEntity(RegUser regUser, LocalDateTime regDate,
		LocalDateTime modDate, long id, String modelName, String description, String modelRepoRealName,
		String workspaceResourceName, String modelPath, StorageEntity storageEntity) {
		super(regUser, regDate, modDate);
		this.id = id;
		this.modelName = modelName;
		this.description = description;
		this.modelRepoRealName = modelRepoRealName;
		this.workspaceResourceName = workspaceResourceName;
		this.modelPath = modelPath;
		this.storageEntity = storageEntity;
	}
	public void setRegUserInfo(RegUser regUser, LocalDateTime regDate, LocalDateTime modDate){
		this.regUser = regUser;
		this.regDate = regDate;
		this.modDate = modDate;
	}
	public void addModelLabelEntity(List<LabelEntity> labelEntityList) {
		List<ModelLabelEntity> modelLabelEntities = labelEntityList.stream()
			.map(labelEntity -> ModelLabelEntity.builder().labelEntity(labelEntity).modelRepoEntity(this).build())
			.toList();

		this.modelLabelEntityList = modelLabelEntities;
	}

	public void addModelVersionEntity(ModelVersionEntity modelVersionEntity){
		if(modelVersionEntity != null){
			this.modelVersionList.add(modelVersionEntity);
		}
	}

	public void setModelPath(String modelPath) {
		this.modelPath = modelPath;
	}
	public void setModelRepoRealName(String modelRepoRealName) {
		this.modelRepoRealName = modelRepoRealName;
	}

	public void updateModelRepoVersion(long versionInfo) {
		this.getModelVersionList()
			.add(ModelVersionEntity.builder()
				.version("v" + versionInfo)
				.modelRepoEntity(this)
				.build());
	}

	public void updateModelRepo(String modelName, String description) {
		this.modelName = modelName;
		this.description = description;
	}

}
