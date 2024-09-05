package com.xiilab.servercore.code.dto;

import java.util.Map;

import com.xiilab.modulecommon.enums.RepositoryAuthType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCodeReqDTO {
	private RepositoryAuthType repositoryAuthType;
	private Long credentialId;
	private String defaultPath;
	private String cmd;
	private Map<String,String> codeArgs;
}
