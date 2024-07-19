package com.xiilab.servercore.preset.dto.response;

import java.util.List;

import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulecommon.util.TypeConversionUtils;
import com.xiilab.modulek8sdb.preset.entity.ResourcePresetEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class ResourcePresetResDTO {
	protected Long id;
	protected String title;
	protected String description;
	protected Float launcherCpuUsage;
	protected Float launcherMemUsage;
	protected Integer gpuUsage;
	protected Float cpuUsage;
	protected Float memUsage;
	protected NodeType nodeType;

	@Getter
	@SuperBuilder
	public static class FindResourcePresetDetail extends ResourcePresetResDTO {
		public static FindResourcePresetDetail from(
			ResourcePresetEntity resourcePresetEntity
		) {
			return FindResourcePresetDetail.builder()
				.id(resourcePresetEntity.getId())
				.title(resourcePresetEntity.getTitle())
				.description(resourcePresetEntity.getDescription())
				.launcherCpuUsage(TypeConversionUtils.toFloat(resourcePresetEntity.getLauncherCpuUsage()))
				.launcherMemUsage(TypeConversionUtils.toFloat(resourcePresetEntity.getLauncherMemUsage()))
				.cpuUsage(TypeConversionUtils.toFloat(resourcePresetEntity.getCpuUsage()))
				.memUsage(TypeConversionUtils.toFloat(resourcePresetEntity.getMemUsage()))
				.gpuUsage(resourcePresetEntity.getGpuUsage())
				.nodeType(resourcePresetEntity.getNodeType())
				.build();
		}
	}

	@Getter
	@Builder
	public static class FindResourcePresets {
		private List<FindResourcePresetDetail> resourcePresets;
		private Long totalCount;

		public static FindResourcePresets from(List<ResourcePresetEntity> presetEntities, Long totalCount) {
			return FindResourcePresets
				.builder()
				.resourcePresets(presetEntities.stream().map(FindResourcePresetDetail::from).toList())
				.totalCount(totalCount)
				.build();
		}

	}

}
