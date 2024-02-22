package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
	private String name;
	private String fileYN;
	private String size;
	private String timestamp;

	@Builder
	public FileDTO(String rawString) {
		//에러체크
		if (rawString.contains("Syntax error") || rawString.contains("stat: cannot statx"))
			throw new IllegalArgumentException("경로를 다시 확인해주세요");
		String[] fileArray = rawString.split(",");
		this.name = getFileName(fileArray[0]);
		this.fileYN = fileArray[1];
		this.size = fileArray[2];
		this.timestamp = fileArray[3];
	}

	public String getFileName(String rawPath) {
		String[] split = rawPath.split("/");
		return split[split.length - 1];
	}
}
