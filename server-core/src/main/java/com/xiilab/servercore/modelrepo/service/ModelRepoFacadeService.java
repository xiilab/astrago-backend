package com.xiilab.servercore.modelrepo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.servercore.deploy.dto.ResDeploys;
import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;

import me.desair.tus.server.upload.UploadInfo;

public interface ModelRepoFacadeService {

	ModelRepoDTO.ResponseDTO createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO);

	PageDTO<ModelRepoDTO.ResponseDTO> getModelRepoList(String workspaceResourceName, String search, int pageNum, int pageSize);

	ModelRepoDTO.ResponseDTO getModelRepoById(String workspaceResourceName, long modelRepoId);

	void deleteModelRepoById(long modelRepoId);

	void registerOrVersionUpModelRepo(List<MultipartFile> files, ModelRepoDTO.WlModelRepoDTO modelRepoDTO);

	void deleteModelRepoVersion(long versionId);

	void updateModelRepoById(long modelRepoId, ModelRepoDTO.UpdateDTO updateDTO);

	PageDTO<ModelRepoDTO.VersionDTO> getModelRepoVersionList(long modelRepoId, int pageNum, int pageSize, String sort);
	ModelRepoEntity getModelRepoEntityById(long modelId);

	ResDeploys getDeploysUsingModel(Long modelRepoId, int pageNum, int pageSize);

	DirectoryDTO getModelFiles(Long modelRepoId, String modelVersion, String filePath);

	ModelRepoDTO.ResponseDTO createModelRepo(ModelRepoDTO.RequestDTO modelRepoDTO, UploadInfo uploadInfo);

	void uploadMetaFiles(Long modelRepoId, String modelVersion, List<MultipartFile> files);
}
