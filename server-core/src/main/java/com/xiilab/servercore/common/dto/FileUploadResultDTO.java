package com.xiilab.servercore.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadResultDTO {
	private final int successCnt;
	private final int failCnt;
}
