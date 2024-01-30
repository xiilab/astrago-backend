package com.xiilab.servercore.dataset.entity;


import com.xiilab.modulek8s.common.enumeration.StorageType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_LOCAL_DATASET")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("LOCAL")
@Getter
public class LocalDatasetEntity extends Dataset {
	@Column(name = "STORAGE_IP")
	private String ip;

	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;

	@Column(name = "STORAGE_PATH")
	private String storagePath;

	@Builder
	public LocalDatasetEntity(String ip, StorageType storageType, String storagePath) {
		this.ip = ip;
		this.storageType = storageType;
		this.storagePath = storagePath;
	}
}
