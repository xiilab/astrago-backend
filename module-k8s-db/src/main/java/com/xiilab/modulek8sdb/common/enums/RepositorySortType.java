package com.xiilab.modulek8sdb.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RepositorySortType {
	NAME("name"),
	CREATED_AT("createdAt"),
	FILE_SIZE("fileSize")
	;

	private final String type;

}
