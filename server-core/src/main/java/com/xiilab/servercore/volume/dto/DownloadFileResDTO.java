package com.xiilab.servercore.volume.dto;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadFileResDTO {
	private String fileName;
	private ByteArrayResource byteArrayResource;
	private MediaType mediaType;
}
