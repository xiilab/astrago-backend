package com.xiilab.servercore.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.RepositoryType;
import com.xiilab.modulek8sdb.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceModelDTO;

public interface ModelService {
	void insertAstragoDataset(AstragoModelEntity astragoModel, List<MultipartFile> files);

	void insertLocalModel(LocalModelEntity localModelEntity);

	ModelDTO.ResModels getModels(int pageNo, int pageSize, UserInfoDTO userInfoDTO);

	ModelDTO.ResModelWithStorage getModelWithStorage(Long modelId);

	Model findById(Long modelId);

	void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId);

	void deleteModelWorkspaceMappingById(Long modelId);

	void deleteModelById(Long modelId);

	DirectoryDTO getAstragoModelFiles(Long modelId, String filePath);

	void astragoModelUploadFile(Long modelId, String path, List<MultipartFile> files);

	void astragoModelCreateDirectory(Long modelId, ModelDTO.ReqFilePathDTO reqFilePathDTO);

	void astragoModelDeleteFiles(Long modelId, ModelDTO.ReqFilePathDTO reqFilePathDTO);

	DownloadFileResDTO DownloadAstragoModelFile(Long modelId, String filePath);

	ModelDTO.ModelsInWorkspace getModelsByRepositoryType(String workspaceResourceName, RepositoryType repositoryType, UserInfoDTO userInfoDTO);

	void insertWorkspaceModel(InsertWorkspaceModelDTO insertWorkspaceModelDTO);

	void deleteWorkspaceModel(String workspaceResourceName, Long modelId, UserInfoDTO userInfoDTO);
}
