package com.xiilab.modulek8s.storage.volume.dto;

import com.xiilab.modulek8s.common.vo.K8SResourceReqDTO;
import com.xiilab.modulek8s.storage.facade.dto.StorageReqDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateVolumeDTO extends K8SResourceReqDTO {
	private StorageType storageType;
	private int requestVolume;
	private String workspaceMetaDataName;
	private String storageClassMetaName;

	@Builder
	public CreateVolumeDTO(String name, String description, StorageType storageType, int requestVolume,
		String workspaceMetaDataName, String storageClassMetaName) {
		super(name, description);
		this.storageType = storageType;
		this.requestVolume = requestVolume;
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.storageClassMetaName = storageClassMetaName;
	}

	public static CreateVolumeDTO storageReqDtoToCreateVolumeDto(StorageReqDTO storageReqDTO){
		return CreateVolumeDTO.builder()
			.name(storageReqDTO.getName())
			.workspaceMetaDataName(storageReqDTO.getWorkspaceMetaDataName())
			.requestVolume(storageReqDTO.getRequestVolume())
			.storageType(storageReqDTO.getStorageType())
			.build();
	}
	public void setStorageClassMetaName(String storageMetaName){
		this.storageClassMetaName = storageMetaName;
	}
}
