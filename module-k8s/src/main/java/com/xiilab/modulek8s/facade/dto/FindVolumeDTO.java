package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.SearchCondition;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindVolumeDTO {
	private SearchCondition searchCondition;

	@Builder
	public FindVolumeDTO(String option, String keyword) {
		this.searchCondition = SearchCondition.builder().option(option).keyword(keyword).build();
	}
}
