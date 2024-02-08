package com.xiilab.servercore.code.dto;

import com.xiilab.servercore.code.entity.CodeEntity;

import lombok.Getter;

@Getter
public class CodeReqDTO {
	private CodeEntity.CodeType codeType;
	private String codeURL;
	private long credentialId;
}
