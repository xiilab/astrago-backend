package com.xiilab.modulek8s.storage.storageclass.repository;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;

import io.fabric8.kubernetes.api.model.storage.StorageClass;

public interface StorageClassRepository {
	StorageClass findStorageClassByType(StorageType storageType);

	void createStorageClass(CreateStorageClassDTO createStorageClassDTO);

	boolean storageClassConnectionTest(String storageType);
}
