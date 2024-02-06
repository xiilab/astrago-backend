package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnnotationField {
	NAME("varName"),
	DESCRIPTION("description"),
	CREATED_AT("created-at"),
	CREATOR_FULL_NAME("creator-varName"),
	TYPE("type"),
	IMAGE_NAME("image-varName"),
	IMAGE_TAG("image-tag"),
	DATASET_NAME("dataset-varName"),
	IMAGE_TYPE("image-repositoryType")
	;

	private final String field;
}
