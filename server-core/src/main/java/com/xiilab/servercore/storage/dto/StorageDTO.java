package com.xiilab.servercore.storage.dto;

import java.time.LocalDate;
import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.facade.dto.SecretDTO;
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
	private SecretDTO secretDTO;

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

		public StorageEntity toEntity(){
			return StorageEntity.builder()
				.storageName(this.storageName)
				.requestVolume(this.requestVolume)
				.description(this.description)
				.storageType(this.storageType)
				.ip(this.ip)
				.storagePath(this.storagePath)
				.hostPath(this.hostPath)
				.astragoDeploymentName(this.astragoDeploymentName)
				.namespace(this.namespace)
				.pvName(this.pvName)
				.pvcName(this.pvcName)
				.volumeName(this.volumeName)
				.build();
		}
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
		private LocalDate createdAt;
		private String creatorName;

		public static ResStorage toDto(StorageEntity storageEntity){
			return ResStorage.builder()
				.storageId(storageEntity.getStorageId())
				.storageName(storageEntity.getStorageName())
				.storageType(storageEntity.getStorageType())
				.ip(storageEntity.getIp())
				.storagePath(storageEntity.getStoragePath())
				.hostPath(storageEntity.getHostPath())
				.requestVolume(storageEntity.getRequestVolume())
				.createdAt(storageEntity.getRegDate().toLocalDate())
				.creatorName(storageEntity.getRegUser().getRegUserRealName())
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

	@Getter
	public static class ModifyStorage{
		private String storageName;
	}
}
