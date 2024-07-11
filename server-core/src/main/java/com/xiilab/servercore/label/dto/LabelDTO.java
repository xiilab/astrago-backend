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

	public LabelEntity convertLabelEntity(String workspaceName) {
		return LabelEntity.builder()
			.name(this.getLabelName())
			.workspaceResourceName(workspaceName)
			.colorCode(this.getColorCode())
			.colorCodeName(this.getColorCodeName())
			.build();
	}
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class RequestDTO extends LabelDTO {
		private long labelId;

		public static RequestDTO convertLabelDTO(LabelEntity labelEntity) {
			return RequestDTO.builder()
				.labelId(labelEntity.getId())
				.labelName(labelEntity.getName())
				.colorCode(labelEntity.getColorCode())
				.colorCodeName(labelEntity.getColorCodeName())
				.build();
		}
	}
}
