package com.xiilab.modulek8s.storage.common.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResDTO {
	private final List<?> content;
	private final int page;
	private final int size;
	private final long totalCount;

	@Builder
	public PageResDTO(List<?> content, int page, int size, long totalCount) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalCount = totalCount;
	}
}
