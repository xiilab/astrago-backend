package com.xiilab.servercore.dataset.service;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatasetFacadeServiceImpl implements DatasetFacadeService{
	private final DatasetService datasetService;
	private final StorageService storageService;
	@Override
	@Transactional
	public void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files) {
		//storage 조회
		StorageEntity storageEntity = storageService.findById(createDatasetDTO.getStorageId());

		//dataset 저장
		AstragoDatasetEntity astragoDataset = AstragoDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.storageEntity(storageEntity)
			.build();

		datasetService.insertAstragoDataset(astragoDataset, files);
	}
}
