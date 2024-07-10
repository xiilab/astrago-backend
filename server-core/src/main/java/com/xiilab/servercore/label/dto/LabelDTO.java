package com.xiilab.servercore.label.dto;

import com.xiilab.modulek8sdb.modelrepo.entity.LabelEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelDTO {
	private long labelId;
	// 라벨은 model에 종속됨
	private String labelName;
	private String colorCode;

	public static LabelDTO convertLabelDTO(LabelEntity labelEntity) {
		return LabelDTO.builder()
			.labelId(labelEntity.getId())
			.labelName(labelEntity.getName())
			.colorCode(labelEntity.getColorCode())
			.build();
	}
}
