package com.xiilab.modulek8s.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchCondition {
	private String option;
	private String keyword;

	@Builder
	public SearchCondition(String option, String keyword) {
		this.option = option;
		this.keyword = keyword;
	}
}
