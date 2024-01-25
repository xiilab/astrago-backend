package com.xiilab.servercore.storage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.repository.StorageRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	private final StorageRepository storageRepository;

	/**
	 * storage 생성
	 * @param createStorageReqDTO
	 */
	@Override
	public void insertStorage(StorageDTO.Create createStorageReqDTO) {
		StorageEntity storageEntity = StorageEntity.toEntity(createStorageReqDTO);
		storageRepository.save(storageEntity);
	}
}
