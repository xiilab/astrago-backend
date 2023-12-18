package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindVolumeDTO {
	private String workspaceMetaName;
	private int pageNumber;
	private int pageSize;
	private String option;
	private String keyword;
	@Builder
	public FindVolumeDTO(String workspaceMetaName, int pageNumber, int pageSize, String option, String keyword) {
		this.workspaceMetaName = workspaceMetaName;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.option = option;
		this.keyword = keyword;
	}
}
