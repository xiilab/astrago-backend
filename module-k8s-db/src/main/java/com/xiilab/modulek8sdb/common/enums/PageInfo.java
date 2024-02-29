package com.xiilab.modulek8sdb.common.enums;

import lombok.Getter;

@Getter
public class PageInfo {
	private Integer page;
	private Integer pageSize;
	public PageInfo(Integer page, Integer pageSize) {
		this.page = page == null ? 1 : page;
		this.pageSize = pageSize == null ? 10 : pageSize;
	}
}
