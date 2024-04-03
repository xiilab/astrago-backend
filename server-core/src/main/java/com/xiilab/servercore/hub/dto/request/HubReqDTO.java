package com.xiilab.servercore.hub.dto.request;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.hub.enums.HubLabelType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubReqDTO {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class SaveHub {
		private String title;
		private String description;
		private Set<HubLabelType> hubLabelTypes;
		private String thumbnailURL;
		private String imageName;
		private String readmeURL;
		private String sourceCodeUrl;
		private String sourceCodeMountPath;
		private String modelMountPath;
		private String datasetMountPath;
		private Map<String, String> envMap;
		private String command;
		private List<HyperParam> hyperParams;
		private WorkloadType workloadType;

		public Map<String, String> convertHypterParmToMap() {
			return hyperParams.stream().collect(Collectors.toMap(HyperParam::getKey, HyperParam::getDescription));
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class HyperParam {
		private String key;
		private String description;
	}
}
