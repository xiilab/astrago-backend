package com.xiilab.servercore.modelrepo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;

public interface ModelRepoFacadeService {

	void createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO, List<MultipartFile> files);

	List<ModelRepoDTO.ResponseDTO> getModelRepoList(String workspaceResourceName);

	ModelRepoDTO.ResponseDTO getModelRepoById(String workspaceResourceName, long modelRepoId);

	void deleteModelRepoById(long modelRepoId);

	void modifyModelRepo(long modelRepoId, ModelRepoDTO.RequestDTO modelRepoDTO);

}
