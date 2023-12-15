package com.xiilab.modulecommon.repository;

import com.fasterxml.jackson.databind.JsonNode;


public interface CommonRepository {
	String formatDateTime(double unixTime);

	String getStringOrNull(JsonNode node, String fieldName);
	String toUnixTime(String formattedDateTime);
}
