package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.common.enumeration.StorageType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateStorageClassDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private String ip;
	private String storageSavePath;
}
