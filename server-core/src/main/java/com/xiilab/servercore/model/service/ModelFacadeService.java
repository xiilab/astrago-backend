package com.xiilab.servercore.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;

public interface ModelFacadeService {
	void insertAstragoDataset(ModelDTO.CreateAstragoModel createModelDTO, List<MultipartFile> files);

	void insertLocalModel(ModelDTO.CreateLocalModel createLocalModel);

	ModelDTO.ResModelWithStorage getModel(Long modelId);

	void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId, UserInfoDTO userInfoDTO);

	void deleteModel(Long modelId, UserInfoDTO userInfoDTO);

	ModelDTO.FileInfo getAstragoModelFileInfo(Long modelId, String filePath);

	DownloadFileResDTO getAstragoModelFile(Long modelId, String filePath);

	DirectoryDTO getLocalModelFiles(Long modelId, String filePath);

	DownloadFileResDTO DownloadLocalModelFile(Long modelId, String filePath);

	ModelDTO.FileInfo getLocalModelFileInfo(Long modelId, String filePath);

	DownloadFileResDTO getLocalModelFile(Long modelId, String filePath);
}
