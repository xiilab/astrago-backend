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
	RESOURCE_NAME("resource-name"),
	STORAGE_NAME("storage-name"),
	CONTROL_BY("control-by"),
	JOB_NAME("job-name"),
	APP("app");

	private final String field;
}
