package com.xiilab.modulek8s.storage.facade.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StorageReqDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;
}
