package com.xiilab.servercore.dataset.dto;

import com.xiilab.modulecommon.enums.FileType;

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
