package com.xiilab.servercore.dataset.entity;


import com.xiilab.servercore.storage.entity.StorageEntity;

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

	@Builder
	public AstragoDatasetEntity(Long datasetId, String datasetName, StorageEntity storageEntity) {
		super(datasetId, datasetName);
		this.storageEntity = storageEntity;
	}
}
