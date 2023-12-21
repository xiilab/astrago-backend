package com.xiilab.moduleuser.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindDTO {
	private SearchCondition searchCondition;

	@Builder
	public FindDTO(String option, String keyword) {
		this.searchCondition = SearchCondition.builder().option(option).keyword(keyword).build();
	}
}
