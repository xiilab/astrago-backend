package com.xiilab.servercore.storage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.storage.volume.dto.response.StorageResDTO;
import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageFacadeServiceImpl implements StorageFacadeService {
	private final StorageService storageService;
	private final StorageModuleService storageModuleService;

	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.deployment-name}")
	private String astragoDeploymentName;
	@Value("${astrago.storage-default-path}")
	private String storageDefaultPath;

	@Override
	public void insertStorage(StorageDTO storageDTO) {
		//1. host에 스토리지 path 디렉토리 생성
		String storageName = storageDTO.getStorageName();
		String path = System.getProperty("user.home") + storageDefaultPath + storageName + "-" + UUID.randomUUID()
			.toString()
			.substring(6);

		Path hostPath = Paths.get(path.replace(" ", ""));
		try {
			Files.createDirectories(hostPath);
		} catch (IOException e) {
			throw new K8sException(StorageErrorCode.STORAGE_DIRECTORY_CREATION_FAILED);
		}

		CreateStorageReqDTO createStorageReqDTO = CreateStorageReqDTO.builder()
			.storageName(storageDTO.getStorageName())
			.storageType(storageDTO.getStorageType())
			.description(storageDTO.getDescription())
			.ip(storageDTO.getIp())
			.storagePath(storageDTO.getStoragePath())
			.requestVolume(storageDTO.getRequestVolume())
			.hostPath(String.valueOf(hostPath))
			.astragoDeploymentName(astragoDeploymentName)
			.namespace(namespace)
			.build();
		StorageResDTO storage = storageModuleService.createStorage(createStorageReqDTO);

		StorageDTO.Create createStorage = StorageDTO.Create.builder()
			.storageName(storageDTO.getStorageName())
			.description(storageDTO.getDescription())
			.storageType(storageDTO.getStorageType())
			.ip(storageDTO.getIp())
			.storagePath(storageDTO.getStoragePath())
			.namespace(storage.getNamespace())
			.hostPath(storage.getHostPath())
			.astragoDeploymentName(storage.getAstragoDeploymentName())
			.volumeName(storage.getVolumeName())
			.pvName(storage.getPvName())
			.pvcName(storage.getPvcName())
			.requestVolume(storageDTO.getRequestVolume())
			.build();
		//db 세팅
		storageService.insertStorage(createStorage);
	}
	@Override
	@Transactional
	public void deleteStorage(Long storageId) {
		StorageEntity storageEntity = storageService.findById(storageId);
		//스토리지 db 데이터 삭제
		storageService.deleteById(storageId);

		//K8s 스토리지 삭제 로직
		DeleteStorageReqDTO deleteStorageReqDTO = DeleteStorageReqDTO.builder()
			.pvcName(storageEntity.getPvcName())
			.pvName(storageEntity.getPvName())
			.volumeName(storageEntity.getVolumeName())
			.namespace(storageEntity.getNamespace())
			.hostPath(storageEntity.getHostPath())
			.astragoDeploymentName(storageEntity.getAstragoDeploymentName())
			.build();
		storageModuleService.deleteStorage(deleteStorageReqDTO);
	}
}
