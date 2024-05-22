package com.xiilab.servercore.dataset.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceDatasetDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceDatasetDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;

public interface DatasetService {
	void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files);

	DatasetDTO.ResDatasets getDatasets(PageInfo pageInfo, RepositorySearchCondition repositorySearchCondition,
		UserDTO.UserInfo userInfoDTO, PageMode pageMode);

	DatasetDTO.ResDatasetWithStorage getDatasetWithStorage(Long datasetId);

	void insertLocalDataset(LocalDatasetEntity localDatasetEntity);

	Dataset findById(Long datasetId);

	void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId);

	void deleteDatasetById(Long datasetId);

	void deleteDatasetWorkspaceMappingById(Long datasetId);

	DirectoryDTO getAstragoDatasetFiles(Long datasetId, String filePath);

	void astragoDatasetUploadFile(Long datasetId, String path, List<MultipartFile> files);

	void astragoDatasetDeleteFiles(Long datasetId, DatasetDTO.ReqFilePathsDTO reqFilePathDTO);

	DownloadFileResDTO downloadAstragoDatasetFile(Long datasetId, String reqFilePathDTO);

	void compressAstragoDatasetFiles(Long datasetId, DatasetDTO.ReqCompressDTO reqCompressDTO);

	void deCompressAstragoDatasetFile(Long datasetId, String filePath);

	void astragoDatasetCreateDirectory(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO);

	DatasetDTO.DatasetsInWorkspace getDatasetsByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO);

	void insertWorkspaceDataset(InsertWorkspaceDatasetDTO insertWorkspaceDatasetDTO);

	void deleteWorkspaceDataset(String workspaceResourceName, Long datasetId, UserDTO.UserInfo userInfoDTO);

	DatasetDTO.DatasetsInWorkspace getDatasetsByWorkspaceResourceName(String workspaceResourceName);

	void deleteDatasetWorkloadMapping(Long jobId);

	void updateWorkspaceDataset(UpdateWorkspaceDatasetDTO updateWorkspaceDatasetDTO, String workspaceResourceName,
		Long datasetId, UserDTO.UserInfo userInfoDTO);

	void deleteDatasetWorkloadMappingById(Long datasetId);
}
