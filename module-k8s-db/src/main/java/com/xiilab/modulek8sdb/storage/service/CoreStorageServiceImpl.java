package com.xiilab.modulek8sdb.storage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.storage.dto.StorageDto;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.storage.repository.StorageRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CoreStorageServiceImpl implements StorageService{
	private final StorageRepository storageRepository;

	@Override
	public List<StorageDto> getStorages() {
		List<StorageEntity> storages = storageRepository.findAll();
		return storages.stream().map(StorageDto::new).toList();
	}

	@Override
	public Optional<StorageEntity> findById(Long storageId) {
		return storageRepository.findById(storageId);
	}
}
