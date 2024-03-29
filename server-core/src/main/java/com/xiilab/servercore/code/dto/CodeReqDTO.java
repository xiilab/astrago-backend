package com.xiilab.servercore.code.dto;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReqDTO {
	private String codeURL;
	private String workspaceName;
	private Long credentialId;
	private RepositoryType repositoryType;
	private RepositoryAuthType repositoryAuthType;

	public CodeReqDTO(ModuleCodeReqDTO moduleCodeReqDTO) {
		this.codeURL = moduleCodeReqDTO.getRepositoryURL();
		this.workspaceName = null;
		this.credentialId = moduleCodeReqDTO.getCredentialId();
		this.repositoryType = moduleCodeReqDTO.getRepositoryType();
		this.repositoryAuthType = moduleCodeReqDTO.getRepositoryAuthType();
	}
}
