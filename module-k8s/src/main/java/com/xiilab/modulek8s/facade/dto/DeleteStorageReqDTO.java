package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulecommon.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteStorageReqDTO {
	private String pvcName;
	private String pvName;
	private String volumeName;
	private String hostPath;
	private String namespace;
	private String astragoDeploymentName;
	private StorageType storageType;
	private String secretName;
	private String storageName;
	private String storageClassName;
}
