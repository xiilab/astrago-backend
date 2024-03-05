package com.xiilab.servercore.code.dto;

import com.xiilab.modulecommon.enums.RepositoryType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodeReqDTO {
	private String codeURL;
	private String workspaceName;
	private long credentialId;
	private RepositoryType repositoryType;
}
