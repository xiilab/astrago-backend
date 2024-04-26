package com.xiilab.modulek8sdb.code.dto;

import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulecommon.enums.CodeType;

import lombok.Getter;

@Getter
public class CodeSearchCondition {
	private String searchText;
	private CodeType codeType;

	public CodeSearchCondition(String searchText, CodeType codeType) {
		this.searchText = searchText == null ? null : searchText;
		this.codeType = codeType == null ? null : codeType;
	}
}
