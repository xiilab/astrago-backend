package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnnotationField {
	NAME("name"),
	WORKSPACE_NAME("ws-name"),
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
	IMAGE_TYPE("image-type")
	;

	private final String field;
}
