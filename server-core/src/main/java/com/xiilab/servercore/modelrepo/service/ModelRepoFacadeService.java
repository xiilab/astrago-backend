package com.xiilab.servercore.modelrepo.service;

import java.util.List;

import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;

public interface ModelRepoFacadeService {

	ModelRepoDTO.ResponseDTO createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO);

	List<ModelRepoDTO.ResponseDTO> getModelRepoList(String workspaceResourceName);

	ModelRepoDTO.ResponseDTO getModelRepoById(String workspaceResourceName, long modelRepoId);

	void deleteModelRepoById(long modelRepoId);

	void modifyModelRepo(long modelRepoId, ModelRepoDTO.RequestDTO modelRepoDTO);

}
