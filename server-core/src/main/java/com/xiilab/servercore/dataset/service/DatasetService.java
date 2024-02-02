package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;

public interface DatasetService {
	void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files);

	DatasetDTO.ResDatasets getDatasets(int pageNo, int pageSize, UserInfoDTO userInfoDTO);

	DatasetDTO.ResDatasetWithStorage getDatasetWithStorage(Long datasetId);

	void insertLocalDataset(LocalDatasetEntity localDatasetEntity);

	Dataset findById(Long datasetId);

	void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId);

	void deleteDatasetById(Long datasetId);

	void deleteDatasetWorkspaceMappingById(Long datasetId);
}
