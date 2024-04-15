package com.xiilab.servercore.code.dto;

import java.util.Map;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;

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
	private String defaultPath;
	private String cmd;
	private Map<String,String> codeArgs;
	private CodeType codeType;
}
