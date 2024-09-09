package com.xiilab.modulek8s.storage.volume.dto.request;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePVC {
	private String pvcName;
	private String namespace;
	private int requestVolume;
	private String volumeName;
	private String storageClassName;
}
