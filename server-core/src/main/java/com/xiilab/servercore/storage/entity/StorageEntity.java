package com.xiilab.servercore.storage.entity;


import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.servercore.common.entity.BaseEntity;
import com.xiilab.servercore.storage.dto.CreateStorageReqDTO;

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

	@Column(name = "STORAGE_TYPE")
	@Enumerated(EnumType.STRING)
	private StorageType storageType;

	@Column(name = "STORAGE_IP")
	private String ip;

	@Column(name = "STORAGE_PATH")
	private String path;
	@Column(name = "DEFAULT_YN")
	private String defaultYN;

	@Builder
	public StorageEntity(StorageType storageType, String ip, String path, String defaultYN) {
		this.storageType = storageType;
		this.ip = ip;
		this.path = path;
		if(defaultYN == null){
			this.defaultYN = "N";
		}else{
			this.defaultYN = defaultYN;
		}
	}

	public static StorageEntity toEntity(CreateStorageReqDTO request){
		return StorageEntity.builder()
			.storageType(request.getStorageType())
			.ip(request.getIp())
			.path(request.getPath())
			.build();
	}
}
