package com.xiilab.servercore.code.dto;

import com.xiilab.servercore.code.enumeration.CodeType;

import lombok.Getter;

@Getter
public class CodeReqDTO {
	private CodeType codeType;
	private String codeURL;
	private long credentialId;
}
