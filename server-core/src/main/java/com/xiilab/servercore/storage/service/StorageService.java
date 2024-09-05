package com.xiilab.servercore.storage.service;

import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface StorageService {

	void insertStorage(StorageDTO.Create createStorageReqDTO);

	StorageDTO.ResStorages getStorages(int pageNo, int pageSize, DeleteYN deleteYN);

	StorageDTO.ResStorage getStorage(Long storageId);

	StorageEntity findById(Long storageId);

	void deleteById(Long storageId);

	void modifyStorage(Long storageId, StorageDTO.ModifyStorage modifyStorage);

	String getDatasetStorageClassName(long id);

	String getModelVolumeStorageClassName(long id);
}
