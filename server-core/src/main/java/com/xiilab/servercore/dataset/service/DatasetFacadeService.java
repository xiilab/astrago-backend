package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;

public interface DatasetFacadeService {
	void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files);

	DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId);

	void insertLocalDataset(DatasetDTO.CreateLocalDataset createDatasetDTO);

	void modifyDataset(DatasetDTO.ModifyDatset modifyDatset, Long datasetId, UserInfoDTO userInfoDTO);

	void deleteDataset(Long datasetId, UserInfoDTO userInfoDTO);

	DirectoryDTO getLocalDatasetFiles(Long datasetId, String filePath);

	DownloadFileResDTO DownloadLocalDatasetFile(Long datasetId, String filePath);

	DatasetDTO.FileInfo getLocalDatasetFileInfo(Long datasetId, String filePath);

	DownloadFileResDTO getLocalDatasetFile(Long datasetId, String filePath);

	DatasetDTO.FileInfo getAstragoDatasetFileInfo(Long datasetId, String filePath);

	DownloadFileResDTO getAstragoDatasetFile(Long datasetId, String filePath);
}
