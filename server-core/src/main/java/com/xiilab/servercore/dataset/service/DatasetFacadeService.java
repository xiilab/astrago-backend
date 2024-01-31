package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.dataset.dto.DatasetDTO;

public interface DatasetFacadeService {
	void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files);

	DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId);
}
