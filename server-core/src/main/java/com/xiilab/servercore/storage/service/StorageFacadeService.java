package com.xiilab.servercore.storage.service;

import com.xiilab.modulek8sdb.storage.dto.StorageDTO;

public interface StorageFacadeService {
	void insertStorage(StorageDTO storageDTO);

	void deleteStorage(Long storageId);
}
