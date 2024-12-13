package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;

public interface DatasetFacadeService {
	void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files);

	DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId);

	void insertLocalDataset(DatasetDTO.CreateLocalDataset createDatasetDTO);

	void modifyDataset(DatasetDTO.ModifyDatset modifyDatset, Long datasetId, UserDTO.UserInfo userInfoDTO);

	void deleteDataset(Long datasetId, UserDTO.UserInfo userInfoDTO);

	DirectoryDTO getLocalDatasetFiles(Long datasetId, String filePath);

	DownloadFileResDTO DownloadLocalDatasetFile(Long datasetId, String filePath);

	FileInfoDTO getLocalDatasetFileInfo(Long datasetId, String filePath);

	DownloadFileResDTO getLocalDatasetFile(Long datasetId, String filePath);

	FileInfoDTO getAstragoDatasetFileInfo(Long datasetId, String filePath);

	DownloadFileResDTO getAstragoDatasetFile(Long datasetId, String filePath);

	WorkloadResDTO.PageUsingDatasetDTO getWorkloadsUsingDataset(PageInfo pageInfo, Long datasetId,
		UserDTO.UserInfo userInfoDTO);
}
