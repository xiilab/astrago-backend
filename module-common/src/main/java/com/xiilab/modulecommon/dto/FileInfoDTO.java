package com.xiilab.modulecommon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FileInfoDTO {
	private String fileName;
	private String size;
	private String lastModifiedTime;
	private String contentPath;

	@Builder(builderClassName = "convertRawString", builderMethodName = "convertRawString")
	FileInfoDTO(String rawString) {
		//에러체크
		if (rawString.contains("Syntax error") || rawString.contains("stat: cannot statx"))
			throw new IllegalArgumentException("경로를 다시 확인해주세요");
		String[] fileArray = rawString.split(",");
		this.fileName = getFileName(fileArray[0]);
		this.contentPath = fileArray[0];
		this.size = fileArray[2];
	}

	public String getFileName(String rawPath) {
		String[] split = rawPath.split("/");
		return split[split.length - 1];
	}
}
