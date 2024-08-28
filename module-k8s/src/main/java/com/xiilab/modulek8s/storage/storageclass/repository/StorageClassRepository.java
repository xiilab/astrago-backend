package com.xiilab.modulek8s.storage.storageclass.repository;

import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;

import io.fabric8.kubernetes.api.model.storage.StorageClass;

public interface StorageClassRepository {
	List<StorageClass> findStorageClassByType(StorageType storageType);

	void createStorageClass(CreateStorageClassDTO createStorageClassDTO);

	StorageClass createDELLStorage(CreateStorageReqDTO createStorageReqDTO, String storageName);

	boolean storageClassConnectionTest(String storageType);

	StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName);

	void modifyStorageClassDTO(ModifyStorageClassDTO modifyStorageClassDTO);

	void deleteStorageClass(String storageClassMetaName);

	List<StorageClassResDTO> findStorageClasses();

	List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes();

	StorageClass createIbmStorage(String secretName);

	void deleteIbmStorage(String storageName);

	void createServiceAccount(String accountName);
}
