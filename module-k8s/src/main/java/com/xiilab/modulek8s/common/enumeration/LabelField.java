package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelField {
	CREATOR_ID("creator-id"),
	IMAGE("image"),
	VOLUMES("volumes"),
	CODES("codes"),
	STORAGE_TYPE("storage-type"),
	RESOURCE_NAME("resource-name"),
	WORKLOAD_RESOURCE_NAME("workload-resource-name"),
	STORAGE_NAME("storage-name"),
	CONTROL_BY("control-by"),
	JOB_NAME("job-name"),
	GPU_NAME("gpu-name"),
	GPU_TYPE("gpu-type"),
	DEPLOY_MODEL_ID("deploy-model-id"),
	APP("app");

	private final String field;
}
