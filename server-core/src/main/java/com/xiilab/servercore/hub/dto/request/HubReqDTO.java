package com.xiilab.servercore.hub.dto.request;

import java.util.Map;
import java.util.Set;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.hub.enums.HubLabelType;

import lombok.AccessLevel;
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
		private Map<String,String> parameter;
		private WorkloadType workloadType;
	}
}
