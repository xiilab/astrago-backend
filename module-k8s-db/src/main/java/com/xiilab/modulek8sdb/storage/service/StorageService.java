package com.xiilab.modulek8sdb.storage.service;

import java.util.List;

import com.xiilab.modulek8sdb.storage.dto.StorageDto;

public interface StorageService {
	List<StorageDto> getStorages();
}
