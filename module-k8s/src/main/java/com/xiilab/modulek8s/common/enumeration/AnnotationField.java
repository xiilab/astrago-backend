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
	CREATOR_ID("creator-id"),
	CREATOR_NAME("creator-name"),
	TYPE("type"),
	IMAGE_NAME("image-name"),
	IMAGE_TAG("image-tag"),
	DATASET_NAME("dataset-name"),
	IMAGE_TYPE("image-type")
	;

	private final String field;
}
