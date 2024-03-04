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
	DATASET_IDS("dataset-ids"),
	MODEL_NAME("model-name"),
	MODEL_IDS("model-ids"),
	CODE_IDS("code-ids"),
	IMAGE_TYPE("image-type"),
	IMAGE_ID("image_id"),
	IMAGE_CREDENTIAL_ID("image-credential-id");

	private final String field;
}
