package com.xiilab.modulek8sdb.dataset.entity;



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
@Table(name = "TB_ASTRAGO_DATASET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ASTRAGO")
@Getter
public class AstragoDatasetEntity extends Dataset {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STORAGE_ID")
	private StorageEntity storageEntity;

	@Column(name = "DATASET_PATH")
	private String datasetPath;
	@Column(name = "SAVE_DIRECTORY_NAME")
	private String saveDirectoryName;

	@Builder
	public AstragoDatasetEntity(Long datasetId, String datasetName, StorageEntity storageEntity, String defaultPath) {
		super(datasetId, datasetName, defaultPath);
		this.storageEntity = storageEntity;
	}
	public void setDatasetPath(String datasetPath){
		this.datasetPath = datasetPath;
	}
	public void setSaveDirectoryName(String saveDirectoryName){
		this.saveDirectoryName = saveDirectoryName;
	}


	@Override
	public boolean isAstragoDataset() {
		return true;
	}

	@Override
	public boolean isLocalDataset() {
		return false;
	}
}
