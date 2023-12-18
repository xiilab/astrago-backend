package com.xiilab.modulecommon.service;

import com.fasterxml.jackson.databind.JsonNode;


public interface CommonService {
	String formatDateTime(double unixTime);

	String getStringOrNull(JsonNode node, String fieldName);
	String toUnixTime(String formattedDateTime);
}
