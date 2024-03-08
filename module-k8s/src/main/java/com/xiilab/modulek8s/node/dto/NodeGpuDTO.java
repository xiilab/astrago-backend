package com.xiilab.modulek8s.node.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeGpuDTO {
	private String nodeName;
	private List<MIGRequestDTO> migRequests;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MIGRequestDTO {
		private List<Integer> gpuIndexs;
		private boolean migEnable;
		private Map<String, Integer> profile;
	}

	public List<Object> convertMap() {
		List<Object> migReqList = new ArrayList<>();
		for (MIGRequestDTO migRequest : migRequests) {
			HashMap<String, Object> migReqInfo = new HashMap<>();
			migReqInfo.put("devices", migRequest.gpuIndexs);
			if (migRequest.migEnable) {
				migReqInfo.put("mig-enabled", true);
				migReqInfo.put("mig-devices", migRequest.profile);
			} else {
				migReqInfo.put("mig-enabled", false);
			}
			migReqList.add(migReqInfo);
		}
		return migReqList;
	}

	public String getMigKey() {
		return String.format("custom-%s", nodeName);
	}
}
