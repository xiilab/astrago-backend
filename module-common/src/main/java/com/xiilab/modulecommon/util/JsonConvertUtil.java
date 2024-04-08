package com.xiilab.modulecommon.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonConvertUtil {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * json 객체를 map으로 convert하는 메소드
	 * @param json
	 * @return
	 */
	public static Map<String, String> convertJsonToMap(String json) {
		try {
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * map객체를 json문자열로 convert하는 메소드
	 * @param map
	 * @return
	 */
	public static String convertMapToJson(Map<String, String> map) {
		try {
			return objectMapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
