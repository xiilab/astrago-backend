package com.xiilab.servercore.storage.service;

import com.xiilab.servercore.storage.dto.StorageDTO;

public interface StorageFacadeService {
	void insertStorage(StorageDTO storageDTO);

	void deleteStorage(Long storageId);

	void modifyStorage(Long storageId, StorageDTO.ModifyStorage modifyStorage);
}
