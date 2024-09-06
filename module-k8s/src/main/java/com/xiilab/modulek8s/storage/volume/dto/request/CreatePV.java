package com.xiilab.modulek8s.storage.volume.dto.request;

import com.xiilab.modulecommon.enums.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePV {
	private String pvName;
	private String pvcName;
	private String ip;
	private String storagePath;
	private String namespace;
	private StorageType storageType;
	private int requestVolume;
	private String arrayId;
	private String dellVolumeId;
}
