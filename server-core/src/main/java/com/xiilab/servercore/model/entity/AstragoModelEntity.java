package com.xiilab.servercore.model.entity;

import com.xiilab.servercore.storage.entity.StorageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ASTRAGO_MODEL")
@NoArgsConstructor
@DiscriminatorValue("ASTRAGO")
@Getter
public class AstragoModelEntity extends Model {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;

	@Column(name = "MODEL_PATH")
	private String modelPath;
	@Column(name = "MODEL_SIZE")
	private Long modelSize;

	@Builder
	public AstragoModelEntity(Long modelId, String modelName, StorageEntity storageEntity) {
		super(modelId, modelName);
		this.storageEntity = storageEntity;
	}

	public void setModelPath(String datasetPath){
		this.modelPath = datasetPath;
	}
	public void setModelSize(long size){
		this.modelSize = size;
	}

	@Override
	public boolean isAstargoModel() {
		return true;
	}

	@Override
	public boolean isLocalModel() {
		return false;
	}
}
