package com.xiilab.modulek8s.workload.dto.request;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortReqDTO {
	private String name;
	private int port;

	// public Map<String, Integer> convertListToMap() {
	// 	Map<String, Integer> objectObjectHashMap = new HashMap<>();
	// 	objectObjectHashMap.put(this.name, this.port);
	// 	return objectObjectHashMap;
	// }
}
