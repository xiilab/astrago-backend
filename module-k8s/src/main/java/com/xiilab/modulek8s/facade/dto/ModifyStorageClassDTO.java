package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ModifyStorageClassDTO extends K8SResourceReqDTO {
	private String storageClassMetaName;

	@Builder
	public ModifyStorageClassDTO(String name, String description, String creatorName, String creator, String storageClassMetaName) {
		super(name, description, creatorName, creator);
		this.storageClassMetaName = storageClassMetaName;
	}
}
