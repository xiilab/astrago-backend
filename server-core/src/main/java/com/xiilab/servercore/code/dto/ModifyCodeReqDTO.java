package com.xiilab.servercore.code.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCodeReqDTO {
	private String defaultPath;
	private String cmd;
	private Map<String,String> codeArgs;
}
