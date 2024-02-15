package com.xiilab.modulek8sdb.storage.dto;

import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

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
		private String namespace;
		private String astragoDeploymentName;
		private String hostPath;
		private String pvName;
		private String pvcName;
		private String volumeName;
		private int requestVolume;
	}

	@Getter
	@Builder
	public static class ResStorage{
		private Long storageId;
		private String storageName;
		private StorageType storageType;
		private String ip;
		private String storagePath;
		private String hostPath;
		private int requestVolume;

		public static ResStorage toDto(StorageEntity storageEntity){
			return ResStorage.builder()
				.storageId(storageEntity.getStorageId())
				.storageName(storageEntity.getStorageName())
				.storageType(storageEntity.getStorageType())
				.ip(storageEntity.getIp())
				.storagePath(storageEntity.getStoragePath())
				.hostPath(storageEntity.getHostPath())
				.requestVolume(storageEntity.getRequestVolume())
				.build();
		}
	}
	@Getter
	@Builder
	public static class ResStorages{
		List<ResStorage> storages;
		long totalCount;

		public static ResStorages entitiesToDtos(List<StorageEntity> storageEntities, long totalCount){
			return ResStorages.builder()
				.storages(storageEntities.stream().map(ResStorage::toDto).toList())
				.totalCount(totalCount)
				.build();
		}
	}
}
