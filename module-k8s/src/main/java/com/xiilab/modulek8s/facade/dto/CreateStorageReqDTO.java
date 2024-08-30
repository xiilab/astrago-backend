package com.xiilab.modulek8s.facade.dto;


import com.xiilab.modulecommon.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStorageReqDTO {
	private String storageName;
	private String description;
	private StorageType storageType;
	private String ip;
	private String storagePath;
	private String namespace;
	private String astragoDeploymentName;
	private String hostPath;
	private String connectionTestImageUrl;
	private int requestVolume;
	private SecretDTO secretDTO;
	private String arrayId;
	private String storagePool;
}
