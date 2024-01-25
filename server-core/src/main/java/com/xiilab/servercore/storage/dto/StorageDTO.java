package com.xiilab.servercore.storage.dto;

import com.xiilab.modulek8s.common.enumeration.StorageType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageDTO {
	private String storageName;
	private String description;
	private StorageType storageType;
	private String ip;
	private String storagePath;
	private int requestVolume;

	@Getter
	@Builder
	public static class Create{
		private String storageName;
		private String description;
		private StorageType storageType;
		private String ip;
		private String storagePath;
		private int requestVolume;
		private String hostPath;
	}
}
