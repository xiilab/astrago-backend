package com.xiilab.servercore.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;

public interface ModelFacadeService {
	void insertAstragoModel(ModelDTO.CreateAstragoModel createModelDTO, List<MultipartFile> files);

	void insertLocalModel(ModelDTO.CreateLocalModel createLocalModel);

	ModelDTO.ResModelWithStorage getModel(Long modelId);

	void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId, UserDTO.UserInfo userInfoDTO);

	void deleteModel(Long modelId, UserDTO.UserInfo userInfoDTO);

	FileInfoDTO getAstragoModelFileInfo(Long modelId, String filePath);

	DownloadFileResDTO getAstragoModelFile(Long modelId, String filePath);

	DirectoryDTO getLocalModelFiles(Long modelId, String filePath);

	DownloadFileResDTO DownloadLocalModelFile(Long modelId, String filePath);

	FileInfoDTO getLocalModelFileInfo(Long modelId, String filePath);

	DownloadFileResDTO getLocalModelFile(Long modelId, String filePath);

	WorkloadResDTO.PageUsingModelDTO getWorkloadsUsingModel(PageInfo pageInfo, Long modelId);
}
