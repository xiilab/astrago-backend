package com.xiilab.modulek8sdb.code.dto;

import com.xiilab.modulek8sdb.code.enums.CodeType;

import lombok.Getter;

@Getter
public class CodeReqDTO {
	private CodeType codeType;
	private String codeURL;
	private String workspaceName;
	private long credentialId;
}
