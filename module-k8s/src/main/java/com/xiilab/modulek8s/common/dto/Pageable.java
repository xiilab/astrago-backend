package com.xiilab.modulek8s.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Pageable {
	private int pageNumber;
	private int pageSize;

	@Builder
	public Pageable(int pageNumber, int pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
}
