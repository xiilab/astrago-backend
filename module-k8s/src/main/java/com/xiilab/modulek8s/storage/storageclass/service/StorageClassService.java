package com.xiilab.modulek8s.storage.storageclass.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.modulek8s.storage.storageclass.repository.StorageClassRepository;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageClassService {
	private final StorageClassRepository storageClassRepository;
	public StorageClass findStorageClassByType(StorageType storageType) {
		return storageClassRepository.findStorageClassByType(storageType);
	}

	public void createStorageClass(CreateStorageClassDTO createStorageClassDTO) {
		storageClassRepository.createStorageClass(createStorageClassDTO);
	}

	public boolean storageClassConnectionTest(String storageType) {
		return storageClassRepository.storageClassConnectionTest(storageType);
	}

	public StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName) {
		return storageClassRepository.findStorageClassByMetaName(storageClassMetaName);
	}

	public void modifyStorageClass(ModifyStorageClassDTO modifyStorageClassDTO) {
		storageClassRepository.modifyStorageClassDTO(modifyStorageClassDTO);
	}

	public void deleteStorageClass(String storageClassMetaName) {
		storageClassRepository.deleteStorageClass(storageClassMetaName);
	}

	public List<StorageClassResDTO> findStorageClasses() {
		return storageClassRepository.findStorageClasses();
	}

	public List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes() {
		return storageClassRepository.findStorageClassesWithVolumes();
	}
}
