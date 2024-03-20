package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Pageable {
	private Integer pageNumber;
	private Integer pageSize;

	@Builder
	public Pageable(Integer pageNumber, Integer pageSize) {
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
	}
}
