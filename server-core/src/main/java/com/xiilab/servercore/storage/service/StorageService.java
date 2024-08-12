package com.xiilab.servercore.storage.service;

import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.dto.StorageDTO;

public interface StorageService {

	void insertStorage(StorageDTO.Create createStorageReqDTO);

	StorageDTO.ResStorages getStorages(int pageNo, int pageSize, DeleteYN deleteYN);

	StorageDTO.ResStorage getStorage(Long storageId);

	StorageEntity getDefaultStorage();

	StorageEntity findById(Long storageId);

	void deleteById(Long storageId);

	void modifyStorage(Long storageId, StorageDTO.ModifyStorage modifyStorage);

}
