package com.xiilab.modulek8s.storage.storageclass.repository;

import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import io.fabric8.kubernetes.api.model.storage.StorageClass;

public interface StorageClassRepository {
	StorageClass findStorageClassByType(StorageType storageType);
}
