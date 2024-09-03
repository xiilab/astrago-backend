package com.xiilab.modulek8sdb.common.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulek8sdb.experiment.dto.ChartDTO;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ListToStringConverter implements AttributeConverter<List<ChartDTO.YAxis>, String> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(List<ChartDTO.YAxis> attribute) {
		try {
			return attribute != null ? objectMapper.writeValueAsString(attribute) : "";
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Error converting list to JSON string", e);
		}
	}

	@Override
	public List<ChartDTO.YAxis> convertToEntityAttribute(String dbData) {
		try {
			return dbData != null && !dbData.isEmpty() ?
				objectMapper.readValue(dbData,
					objectMapper.getTypeFactory().constructCollectionType(List.class, ChartDTO.YAxis.class)) : null;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error converting JSON string to list", e);
		}
	}
}
