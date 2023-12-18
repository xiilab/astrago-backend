package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.Pageable;
import com.xiilab.modulek8s.common.dto.SearchCondition;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageFindVolumeDTO {
	private String workspaceMetaName;
	private Pageable pageable;
	private SearchCondition searchCondition;
	@Builder
	public PageFindVolumeDTO(String workspaceMetaName, int pageNumber, int pageSize, String option, String keyword) {
		this.workspaceMetaName = workspaceMetaName;
		this.pageable = Pageable.builder().pageNumber(pageNumber).pageSize(pageSize).build();
		this.searchCondition = SearchCondition.builder().option(option).keyword(keyword).build();
	}
}
