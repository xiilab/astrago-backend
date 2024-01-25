package com.xiilab.servercore.storage.service;

import com.xiilab.servercore.storage.dto.StorageDTO;

public interface StorageService {

	void insertStorage(StorageDTO.Create createStorageReqDTO);
}
