package com.xiilab.modulek8s.storage.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PageResDTO {
	private List<?> content;
	private int page;
	private int size;
	private long totalCount;

	@Builder
	public PageResDTO(List<?> content, int page, int size, long totalCount) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalCount = totalCount;
	}
}
