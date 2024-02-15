package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulecommon.enums.StorageType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateVolumeDTO extends K8SResourceReqDTO {
	private String storageClassMetaName;
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;
}
