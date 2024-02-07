package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnnotationField {
	NAME("name"),
	DESCRIPTION("description"),
	CREATED_AT("created-at"),
	CREATOR_FULL_NAME("creator-name"),
	TYPE("type"),
	IMAGE_NAME("image-name"),
	IMAGE_TAG("image-tag"),
	DATASET_NAME("dataset-name"),
	IMAGE_TYPE("image-repositoryType")
	;

	private final String field;
}
