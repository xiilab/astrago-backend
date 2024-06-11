package com.xiilab.servercore.preset.dto.request;

import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulecommon.util.ValidUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class ResourcePresetReqDTO {
	protected String title;
	protected String description;
	protected Float launcherCpuUsage;
	protected Float launcherMemUsage;
	protected Integer gpuUsage;
	protected Float cpuUsage;
	protected Float memUsage;
	protected NodeType nodeType;

	@Getter
	@NoArgsConstructor
	public static class SaveResourcePreset extends ResourcePresetReqDTO {
	}

	@Getter
	@NoArgsConstructor
	public static class UpdateResourcePreset extends ResourcePresetReqDTO {
		private Long id;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class FindSearchCondition {
		private Integer page;
		private Integer size;
		private NodeType nodeType;

		public void setPage(Integer page) {
			this.page = !ValidUtils.isNullOrZero(page) ? page - 1 : 0;
		}

		public void setSize(Integer size) {
			this.size = !ValidUtils.isNullOrZero(size) ? size : Integer.MAX_VALUE;
		}
	}
}
