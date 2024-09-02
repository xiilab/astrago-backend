package com.xiilab.modulek8s.storage.volume.dto.response;

import com.xiilab.modulecommon.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StorageResDTO {
	private String storageName;
	private String description;
	private StorageType storageType;
	private String ip;
	private String storagePath;
	private String namespace;
	private String astragoDeploymentName;
	private String hostPath;
	private String pvName;
	private String pvcName;
	private String volumeName;
	private int requestVolume;
	private String storageClassName;
}
