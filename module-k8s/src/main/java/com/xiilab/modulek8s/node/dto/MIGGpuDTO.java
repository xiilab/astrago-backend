package com.xiilab.modulek8s.node.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.MigStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MIGGpuDTO {
	protected String nodeName;
	protected List<MIGInfoDTO> migInfos;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MIGInfoDTO {
		protected List<Integer> gpuIndexs;
		protected boolean migEnable;
		protected Map<String, Integer> profile;
	}
	@Getter
	@AllArgsConstructor
	@SuperBuilder
	public static class MIGInfoStatus extends MIGGpuDTO{
		private MigStatus status;
	}


	public List<Object> convertToMap() {
		List<Object> migReqList = new ArrayList<>();
		for (MIGInfoDTO migRequest : migInfos) {
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
