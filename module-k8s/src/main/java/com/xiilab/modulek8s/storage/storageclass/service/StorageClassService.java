package com.xiilab.modulek8s.storage.storageclass.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
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
}
