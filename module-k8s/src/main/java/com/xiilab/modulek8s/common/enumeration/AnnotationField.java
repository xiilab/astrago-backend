package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnnotationField {
	NAME("name"),
	WORKSPACE_NAME("ws-name"),
	WORKSPACE_RS_NAME("ws-resource-name"),
	DESCRIPTION("description"),
	CREATED_AT("created-at"),
	CREATOR_FULL_NAME("creator-full-name"),
	CREATOR_USER_NAME("creator-user-name"),
	TYPE("type"),
	IMAGE_NAME("image-name"),
	IMAGE_TAG("image-tag"),
	DATASET_NAME("dataset-name"),
	// TODO 삭제 예정
	// DATASET_IDS("dataset-ids"),
	MODEL_NAME("model-name"),
	// TODO 삭제 예정
	// MODEL_IDS("model-ids"),
	CODE_IDS("code-ids"),
	VOLUME_IDS("volume-ids"),
	LABEL_IDS("label-ids"),
	IMAGE_TYPE("image-type"),
	IMAGE_ID("image_id"),
	IMAGE_CREDENTIAL_ID("image-credential-id"),
	IDE("ide"),
	PARAMETER("parameter"),
	ESTIMATED_INITIAL_TIME("estimated_initial_time"),
	ESTIMATED_REMAINING_TIME("estimated_remaining_time"),
	GPU_TYPE("gpu-type"),
	GPU_NAME("gpu-name"),
	GPU_ONE_PER_MEMORY("gpu-one-per-memory"),
	NODE_NAME("node-name"),
	RESOURCE_PRESET_ID("resource-preset-id"),
	DEPLOY_TYPE("deploy-type"),
	DEPLOY_MODEL_ID("deploy-model-id"),
	DEPLOY_MODEL_VERSION("deploy-model-version"),
	DEPLOY_MODEL_PATH("deploy-model-path"),
	DEPLOY_MODEL_STORAGE_ID("deploy-model-storage-id"),
	;

	private final String field;
}
