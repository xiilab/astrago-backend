package com.xiilab.modulek8s.workload.dto.request;

import java.io.File;

import com.xiilab.modulek8s.common.enumeration.RepositoryAuthType;
import com.xiilab.modulek8s.workload.vo.JobCodeVO;

public record ModuleCodeReqDTO(
	String repositoryURL,   // repository URL
	String branch,          // repository branch
	String mountPath,        // 소스코드 마운트 경로
	RepositoryAuthType repositoryAuthType,
	ModuleCredentialReqDTO credentialReqDTO
){
	public JobCodeVO toJobCodeVO(String workspace) {
		String projectName = repositoryURL.substring(repositoryURL.lastIndexOf("/") + 1).split("\\.")[0];
		if (repositoryAuthType == RepositoryAuthType.PRIVATE && credentialReqDTO != null) {
			return new JobCodeVO(repositoryURL, branch, mountPath + File.separator + projectName, credentialReqDTO.toCredentialVO(workspace));
		} else {
			return new JobCodeVO(repositoryURL, branch, mountPath);
		}
	}
}
