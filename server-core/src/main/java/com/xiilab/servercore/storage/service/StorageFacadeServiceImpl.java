package com.xiilab.servercore.storage.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.storage.volume.dto.response.StorageResDTO;
import com.xiilab.modulek8s.workload.secret.service.SecretService;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.dto.StorageDTO;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StorageFacadeServiceImpl implements StorageFacadeService {
	private final StorageService storageService;
	private final StorageModuleService storageModuleService;
	private final NetworkRepository networkRepository;
	private final SecretService secretService;
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.deployment-name}")
	private String astragoDeploymentName;
	@Value("${astrago.storage-default-path}")
	private String storageDefaultPath;
	@Value("${astrago.private-registry-url}")
	private String privateRegistryUrl;

	public static String getBase64EncodeString(String content){
		// Base64 인코딩
		return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
	}
	@Override
	@Transactional
	public void deleteStorage(Long storageId) {
		StorageEntity storageEntity = storageService.findById(storageId);
		//스토리지를 사용중이면 삭제 안되게
		List<Dataset> datasets = datasetRepository.findByStorageId(storageEntity);
		List<Model> models = modelRepository.findByStorageId(storageEntity);
		if (!datasets.isEmpty() || !models.isEmpty()) {
			throw new RestApiException(StorageErrorCode.FAILD_DELETE_USING_STORAGE);
		}
		//K8s 스토리지 삭제 로직
		DeleteStorageReqDTO deleteStorageReqDTO = DeleteStorageReqDTO.builder()
			.pvcName(storageEntity.getPvcName())
			.pvName(storageEntity.getPvName())
			.volumeName(storageEntity.getVolumeName())
			.namespace(storageEntity.getNamespace())
			.hostPath(storageEntity.getHostPath())
			.astragoDeploymentName(storageEntity.getAstragoDeploymentName())
			.storageType(storageEntity.getStorageType())
			.secretName(storageEntity.getSecretName())
			.storageName(storageEntity.getStorageName())
			.build();
		storageModuleService.deleteStorage(deleteStorageReqDTO);

		//스토리지 db 데이터 삭제
		storageService.deleteById(storageId);
	}

	@Override
	@Transactional
	public void modifyStorage(Long storageId, StorageDTO.ModifyStorage modifyStorage) {
		//스토리지 테이블 수정
		storageService.modifyStorage(storageId, modifyStorage);
	}

	public static String getBase64DecodeString(String content){
		byte[] decodedBytes = Base64.getDecoder().decode(content);
		return new String(decodedBytes, StandardCharsets.UTF_8);
	}

	@Override
	public void insertStorage(StorageDTO storageDTO) {
		//1. host에 스토리지 path 디렉토리 생성
		Path hostPath = createPath(storageDTO.getStorageName());
		//폐쇄망 확인 후 connection image url 조회
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		log.info("폐쇄망 : " + network.getNetworkCloseYN());
		String volumeImageURL = "";
		if(network.getNetworkCloseYN() == NetworkCloseYN.Y){
			if(isBlankSafe(privateRegistryUrl)){
				volumeImageURL = network.getLocalVolumeImageUrl();
			}else{
				volumeImageURL = privateRegistryUrl + "/" + network.getLocalVolumeImageUrl();
			}
		}else{
			volumeImageURL = network.getLocalVolumeImageUrl();
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
			.connectionTestImageUrl(volumeImageURL)
			.secretDTO(storageDTO.getSecretDTO())
			.build();
		if(storageDTO.getStorageType() == StorageType.NFS){
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
		}else if(storageDTO.getStorageType() == StorageType.IBM){
			String secretName = secretService.createIbmSecret(createStorageReqDTO.getSecretDTO());
			StorageClass ibmStorage = storageModuleService.createIbmStorage(secretName);
			PersistentVolumeClaim ibmPvc = storageModuleService.createIbmPvc(ibmStorage.getMetadata().getName());

			StorageDTO.Create createStorage = StorageDTO.Create.builder()
				.storageName(storageDTO.getStorageName())
				.description(storageDTO.getDescription())
				.storageType(storageDTO.getStorageType())
				.ip(storageDTO.getIp())
				.storagePath(storageDTO.getStoragePath())
				.namespace(ibmStorage.getMetadata().getNamespace())
				.pvcName(ibmPvc.getMetadata().getName())
				.requestVolume(storageDTO.getRequestVolume())
				.secretName(secretName)
				.build();
			storageService.insertStorage(createStorage);
		}

	}

	private Path createPath(String storageName) {
		String path = System.getProperty("user.home") + storageDefaultPath + storageName + "-" + UUID.randomUUID()
			.toString()
			.substring(6);

		Path hostPath = Paths.get(path.replace(" ", ""));

		createDirectories(hostPath);

		return hostPath;
	}

	private void createDirectories(Path hostPath){
		try {
			Files.createDirectories(hostPath);
		} catch (IOException e) {
			throw new K8sException(StorageErrorCode.STORAGE_DIRECTORY_CREATION_FAILED);
		}
	}
	// null 체크와 함께 isBlank를 수행하는 메서드
	public static boolean isBlankSafe(String str) {
		return str == null || str.isBlank();
	}


}
