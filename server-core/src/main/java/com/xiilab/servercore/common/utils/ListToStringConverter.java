package com.xiilab.servercore.common.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ListToStringConverter implements AttributeConverter<List<String>, String> {
	private static final String SPLIT_CHAR = ";";

	@Override
	public String convertToDatabaseColumn(List<String> strings) {
		return strings != null ? String.join(SPLIT_CHAR, strings) : "";
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return dbData != null ? Arrays.stream(dbData.split(SPLIT_CHAR))
			.collect(Collectors.toList()) : null;
	}
}
