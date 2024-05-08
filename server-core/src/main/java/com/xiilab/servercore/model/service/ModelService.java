package com.xiilab.servercore.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceModelDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceModelDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;

public interface ModelService {
	void insertAstragoModel(AstragoModelEntity astragoModel, List<MultipartFile> files);

	void insertLocalModel(LocalModelEntity localModelEntity);

	ModelDTO.ResModels getModels(PageInfo pageInfo, RepositorySearchCondition repositorySearchCondition, UserDTO.UserInfo userInfoDTO,
		PageMode pageMode);

	ModelDTO.ResModelWithStorage getModelWithStorage(Long modelId);

	Model findById(Long modelId);

	void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId);

	void deleteModelWorkspaceMappingById(Long modelId);

	void deleteModelById(Long modelId);

	DirectoryDTO getAstragoModelFiles(Long modelId, String filePath);

	void astragoModelUploadFile(Long modelId, String path, List<MultipartFile> files);

	void astragoModelCreateDirectory(Long modelId, ModelDTO.ReqFilePathDTO reqFilePathDTO);

	void astragoModelDeleteFiles(Long modelId, ModelDTO.ReqFilePathsDTO reqFilePathsDTO);

	DownloadFileResDTO downloadAstragoModelFile(Long modelId, String filePath);

	void compressAstragoModelFiles(Long modelId, List<String> filePaths, CompressFileType compressFileType);

	void deCompressAstragoModelFile(Long modelId, String filePath);

	ModelDTO.ModelsInWorkspace getModelsByRepositoryType(String workspaceResourceName, RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO);

	void insertWorkspaceModel(InsertWorkspaceModelDTO insertWorkspaceModelDTO);

	void deleteWorkspaceModel(String workspaceResourceName, Long modelId, UserDTO.UserInfo userInfoDTO);

	ModelDTO.ModelsInWorkspace getModelsByWorkspaceResourceName(String workspaceResourceName);

	void deleteModelWorkloadMapping(Long jobId);

	void updateWorkspaceModel(UpdateWorkspaceModelDTO updateWorkspaceModelDTO, String workspaceResourceName, Long modelId, UserDTO.UserInfo userInfoDTO);
}
