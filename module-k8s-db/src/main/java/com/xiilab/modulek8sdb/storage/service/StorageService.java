package com.xiilab.modulek8sdb.storage.service;

import java.util.List;
import java.util.Optional;

import com.xiilab.modulek8sdb.storage.dto.StorageDto;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface StorageService {
	List<StorageDto> getStorages();

	Optional<StorageEntity> findById(Long storageId);
}
