package com.xiilab.servercore.storage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.repository.StorageRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
	private final StorageRepository storageRepository;

	/**
	 * storage 생성
	 * @param createStorageReqDTO
	 */
	@Override
	@Transactional
	public void insertStorage(StorageDTO.Create createStorageReqDTO) {
		StorageEntity storageEntity = StorageEntity.toEntity(createStorageReqDTO);
		storageRepository.save(storageEntity);
	}

	@Override
	public StorageDTO.ResStorages getStorages(int pageNo, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Page<StorageEntity> storages = storageRepository.findAll(pageRequest);
		List<StorageEntity> entities = storages.getContent();
		long totalCount = storages.getTotalElements();

		return StorageDTO.ResStorages.entitiesToDtos(entities, totalCount);
	}

	@Override
	public StorageDTO.ResStorage getStorage(Long storageId) {
		StorageEntity storageEntity = storageRepository.findById(storageId)
			.orElseThrow(() -> new RuntimeException("스토리지가 존재하지 않습니다."));
		return StorageDTO.ResStorage.toDto(storageEntity);
	}

	@Override
	public StorageEntity findById(Long storageId) {
		return storageRepository.findById(storageId).orElseThrow(() -> new RuntimeException("스토리지가 존재하지 않습니다."));
	}

	@Override
	public void deleteById(Long storageId) {
		storageRepository.deleteById(storageId);
	}
}
