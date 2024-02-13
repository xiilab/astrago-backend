package com.xiilab.servercore.dataset.dto;

import java.io.File;

import com.xiilab.servercore.common.enums.FileType;

import lombok.Getter;

@Getter
public class NginxFilesDTO {
	private String name;
	private String type;
	private String mtime;
	private String size;

	public FileType getFileType() {
		return FileType.D.getType().equalsIgnoreCase(this.type) ? FileType.D : FileType.F;
	}
}
