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
}
