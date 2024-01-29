package com.xiilab.servercore.storage.service;

import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.servercore.storage.entity.StorageEntity;

public interface StorageService {

	void insertStorage(StorageDTO.Create createStorageReqDTO);

	StorageDTO.ResStorages getStorages(int pageNo, int pageSize);

	StorageDTO.ResStorage getStorage(Long storageId);

	StorageEntity findById(Long storageId);

	void deleteById(Long storageId);
}
