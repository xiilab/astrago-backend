package com.xiilab.modulek8s.storage.storageclass.repository;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;

import io.fabric8.kubernetes.api.model.storage.StorageClass;

public interface StorageClassRepository {
	StorageClass findStorageClassByType(StorageType storageType);

	void createStorageClass(CreateStorageClassDTO createStorageClassDTO);

	boolean storageClassConnectionTest(String storageType);

	StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName);

	void modifyStorageClassDTO(ModifyStorageClassDTO modifyStorageClassDTO);

	void deleteStorageClass(String storageClassMetaName);
}
