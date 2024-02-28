package com.xiilab.modulek8sdb.common.enums;

import lombok.Getter;

@Getter
public class PageInfo {
	private Integer page;
	private Integer pageSize;
	public PageInfo(Integer page, Integer pageSize) {
		this.page = page == null ? 0 : page;
		this.pageSize = pageSize == null ? 0 : pageSize;
	}
}
