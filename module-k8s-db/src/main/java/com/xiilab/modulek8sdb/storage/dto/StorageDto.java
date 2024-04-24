package com.xiilab.modulek8sdb.storage.dto;

import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageDto {
	private String volumeName;
	private String hostPath;
	private String pvcName;

	public StorageDto(StorageEntity storageEntity) {
		this.volumeName = storageEntity.getVolumeName();
		this.hostPath = storageEntity.getHostPath();
		this.pvcName = storageEntity.getPvcName();
	}
}
