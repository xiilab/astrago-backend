package com.xiilab.servercore.label.dto;

import com.xiilab.modulek8sdb.label.entity.LabelEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LabelDTO {
	protected String labelName;
	protected String colorCode;
	protected String colorCodeName;

	public LabelEntity convertLabelEntity(String workspaceName, int count) {
		return LabelEntity.builder()
			.name(this.getLabelName())
			.workspaceResourceName(workspaceName)
			.colorCode(this.getColorCode())
			.colorCodeName(this.getColorCodeName())
			.order(count + 1)
			.build();
	}
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends LabelDTO {
		private long labelId;
		private int order;
		public static ResponseDTO convertLabelDTO(LabelEntity labelEntity) {
			return ResponseDTO.builder()
				.labelId(labelEntity.getId())
				.labelName(labelEntity.getName())
				.colorCode(labelEntity.getColorCode())
				.colorCodeName(labelEntity.getColorCodeName())
				.order(labelEntity.getOrder())
				.build();
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class UpdateDTO extends LabelDTO {
		private long labelId;
		private int order;
	}
}
