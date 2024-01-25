package com.xiilab.modulek8s.facade.dto;


import com.xiilab.modulek8s.common.enumeration.StorageType;

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
	private int requestVolume;
}
