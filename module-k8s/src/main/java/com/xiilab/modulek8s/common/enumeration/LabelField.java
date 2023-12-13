package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelField {
	CREATOR("creator"),
	IMAGE("image"),
	VOLUMES("volumes"),
	CODES("codes"),
	STORAGE_TYPE("storage-type");

	private final String field;
}
