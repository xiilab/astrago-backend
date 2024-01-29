package com.xiilab.servercore.storage.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.servercore.storage.dto.StorageDTO;

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
		Path hostPath = Paths.get(
			System.getProperty("user.home") + storageDefaultPath + storageName + "-" + UUID.randomUUID().toString().substring(6));
		try {
			Files.createDirectories(hostPath);
		} catch (IOException e) {
			throw new RuntimeException("스토리지 전용 디렉토리 생성을 실패했습니다.");
		}
		StorageDTO.Create createStorage = StorageDTO.Create.builder()
			.storageName(storageDTO.getStorageName())
			.description(storageDTO.getDescription())
			.storageType(storageDTO.getStorageType())
			.ip(storageDTO.getIp())
			.storagePath(storageDTO.getStoragePath())
			.hostPath(String.valueOf(hostPath))
			.requestVolume(storageDTO.getRequestVolume())
			.build();
		//db 세팅
		storageService.insertStorage(createStorage);

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
		storageModuleService.createStorage(createStorageReqDTO);
	}
}
