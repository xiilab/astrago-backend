package com.xiilab.modulek8s.workload.dto.request;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulek8s.workload.vo.JobCodeVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class ModuleCodeReqDTO {
	@Setter
	Long codeId;
	String repositoryURL;   // repository URL
	String branch;          // repository branch
	String mountPath;        // 소스코드 마운트 경로
	RepositoryType repositoryType;
	RepositoryAuthType repositoryAuthType;
	Long credentialId;
	@Setter
	@JsonIgnore
	ModuleCredentialReqDTO credentialReqDTO;

	public JobCodeVO toJobCodeVO(String workspace) {
		// String projectName = repositoryURL.substring(repositoryURL.lastIndexOf("/") + 1).split("\\.")[0];
		if (repositoryAuthType == RepositoryAuthType.PRIVATE && credentialReqDTO != null) {
			return new JobCodeVO(codeId, repositoryURL, branch, mountPath, credentialReqDTO.toCredentialVO(workspace));
		} else {
			return new JobCodeVO(codeId, repositoryURL, branch, mountPath);
		}
	}
}
