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
	STORAGE_TYPE("storage-repositoryType"),
	RESOURCE_NAME("resource-varName"),
	STORAGE_NAME("storage-varName"),
	CONTROL_BY("control-by"),
	JOB_NAME("job-varName"),
	APP("app");

	private final String field;
}
