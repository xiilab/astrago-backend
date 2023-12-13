package com.xiilab.modulek8s.storage.volume.dto;

import com.xiilab.modulek8s.storage.facade.dto.StorageReqDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateVolumeDTO {
	private String volumeName;
	private String workspaceMetaDataName;
	private int requestVolume;
	private String storageMetaName;

	@Builder
	public CreateVolumeDTO(String volumeName, String workspaceMetaDataName, int requestVolume, String storageMetaName) {
		this.volumeName = volumeName;
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.requestVolume = requestVolume;
		this.storageMetaName = storageMetaName;
	}
	public static CreateVolumeDTO storageReqDtoToCreateVolumeDto(StorageReqDTO storageReqDTO){
		return CreateVolumeDTO.builder()
			.volumeName(storageReqDTO.getVolumeName())
			.workspaceMetaDataName(storageReqDTO.getWorkspaceMetaDataName())
			.requestVolume(storageReqDTO.getRequestVolume())
			.build();
	}
	public void setStorageClassMetaName(String storageMetaName){
		this.storageMetaName = storageMetaName;
	}
}
