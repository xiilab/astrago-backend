package com.xiilab.modulek8sdb.model.entity;


import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ASTRAGO_MODEL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ASTRAGO")
@Getter
public class AstragoModelEntity extends Model {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;

	@Column(name = "MODEL_PATH")
	private String modelPath;

	@Builder
	public AstragoModelEntity(Long modelId, String modelName, StorageEntity storageEntity, String defaultPath) {
		super(modelId, modelName, defaultPath);
		this.storageEntity = storageEntity;
	}

	public void setModelPath(String datasetPath){
		this.modelPath = datasetPath;
	}

	@Override
	public boolean isAstragoModel() {
		return true;
	}

	@Override
	public boolean isLocalModel() {
		return false;
	}
}
