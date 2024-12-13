package com.xiilab.servercore.storage.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8sdb.common.enums.DefaultStorageYN;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.storage.repository.StorageRepository;
import com.xiilab.servercore.storage.dto.StorageDTO;

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
		// 첫번재로 등록하는 스토리지는 default 스토리지로 등록
		if (storageRepository.findAll().size() == 0) {
			createStorageReqDTO.setDefaultStorageYN(DefaultStorageYN.Y);
		}
		StorageEntity storageEntity = createStorageReqDTO.toEntity();
		storageRepository.save(storageEntity);
	}

	@Override
	public StorageDTO.ResStorages getStorages(int pageNo, int pageSize, DeleteYN deleteYN) {
		PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
		Page<StorageEntity> storages = storageRepository.findByDeleteYN(deleteYN, pageRequest);
		List<StorageEntity> entities = storages.getContent();
		long totalCount = storages.getTotalElements();

		return StorageDTO.ResStorages.entitiesToDtos(entities, totalCount);
	}

	@Override
	public StorageDTO.ResStorage getStorage(Long storageId) {
		StorageEntity storageEntity = storageRepository.findById(storageId)
			.orElseThrow(() -> new K8sException(StorageErrorCode.STORAGE_NOT_FOUND));
		return StorageDTO.ResStorage.toDto(storageEntity);
	}

	@Override
	public StorageEntity getDefaultStorage() {
		return storageRepository.findByDefaultStorageYN(DefaultStorageYN.Y)
			.orElseThrow(() -> new RestApiException(StorageErrorCode.STORAGE_NOT_FOUND));
	}

	@Override
	public StorageEntity findById(Long storageId) {
		return storageRepository.findById(storageId)
			.orElseThrow(() -> new K8sException(StorageErrorCode.STORAGE_NOT_FOUND));
	}

	@Override
	@Transactional
	public void deleteById(Long storageId) {
		storageRepository.deleteById(storageId);
	}

	@Override
	@Transactional
	public void modifyStorage(Long storageId, StorageDTO.ModifyStorage modifyStorage) {
		StorageEntity storageEntity = storageRepository.findById(storageId)
			.orElseThrow(() -> new K8sException(StorageErrorCode.STORAGE_NOT_FOUND));
		storageEntity.changeStorageName(modifyStorage.getStorageName());
	}

	@Override
	public StorageEntity getStorageClassByVolumeId(long volumeId) {
		return storageRepository.getStorageClassByVolumeId(volumeId);
	}
}
