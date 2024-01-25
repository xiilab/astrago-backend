package com.xiilab.servercore.storage.entity;


import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.servercore.common.entity.BaseEntity;
import com.xiilab.servercore.storage.dto.StorageDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_STORAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StorageEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "STORAGE_ID")
	private Long storageId;
	@Column(name = "STORAGE_NAME")
	private String storageName;
	@Column(name = "STORAGE_REQUEST_VOLUME")
	private int requestVolume;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;
	@Column(name = "STORAGE_IP")
	private String ip;
	@Column(name = "STORAGE_PATH")
	private String storagePath;
	@Column(name = "HOST_PATH")
	private String hostPath;

	@Builder
	public StorageEntity(Long storageId, String storageName, int requestVolume, String description,
		StorageType storageType, String ip, String storagePath, String hostPath) {
		this.storageId = storageId;
		this.storageName = storageName;
		this.requestVolume = requestVolume;
		this.description = description;
		this.storageType = storageType;
		this.ip = ip;
		this.storagePath = storagePath;
		this.hostPath = hostPath;
	}

	public static StorageEntity toEntity(StorageDTO.Create create){
		return StorageEntity.builder()
			.storageName(create.getStorageName())
			.description(create.getDescription())
			.storageType(create.getStorageType())
			.ip(create.getIp())
			.requestVolume(create.getRequestVolume())
			.storagePath(create.getStoragePath())
			.hostPath(create.getHostPath())
			.build();
	}
}
