package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.RepositoryType;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.modulek8sdb.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceDatasetDTO;

public interface DatasetService {
	void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files);

	DatasetDTO.ResDatasets getDatasets(int pageNo, int pageSize, UserInfoDTO userInfoDTO);

	DatasetDTO.ResDatasetWithStorage getDatasetWithStorage(Long datasetId);

	void insertLocalDataset(LocalDatasetEntity localDatasetEntity);

	Dataset findById(Long datasetId);

	void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId);

	void deleteDatasetById(Long datasetId);

	void deleteDatasetWorkspaceMappingById(Long datasetId);

	DirectoryDTO getAstragoDatasetFiles(Long datasetId, String filePath);

	void astragoDatasetUploadFile(Long datasetId, String path, List<MultipartFile> files);

	void astragoDatasetDeleteFiles(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO);

	DownloadFileResDTO DownloadAstragoDatasetFile(Long datasetId, String reqFilePathDTO);

	void astragoDatasetCreateDirectory(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO);

	DatasetDTO.DatasetsInWorkspace getDatasetsByRepositoryType(String workspaceResourceName, RepositoryType repositoryType, UserInfoDTO userInfoDTO);

	void insertWorkspaceDataset(InsertWorkspaceDatasetDTO insertWorkspaceDatasetDTO);

	void deleteWorkspaceDataset(String workspaceResourceName, Long datasetId, UserInfoDTO userInfoDTO);

	DatasetDTO.DatasetsInWorkspace getDatasetsByWorkspaceResourceName(String workspaceResourceName);
}
