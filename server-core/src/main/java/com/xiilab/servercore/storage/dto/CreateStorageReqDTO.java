package com.xiilab.servercore.storage.dto;



import com.xiilab.modulecommon.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateStorageReqDTO{
	private String storageName;
	private StorageType storageType;
	private String ip;
	private String path;

	@Builder
	public CreateStorageReqDTO(String storageName, StorageType storageType, String ip, String path) {
		this.storageName = storageName;
		this.storageType = storageType;
		this.ip = ip;
		this.path = path;
	}
}
