package com.xiilab.modulek8sdb.common.enums;

import lombok.Getter;

@Getter
public class PageInfo {
	private Integer pageNo;
	private Integer pageSize;
	public PageInfo(Integer pageNo, Integer pageSize) {
		this.pageNo = pageNo == null ? 1 : pageNo;
		this.pageSize = pageSize == null ? 10 : pageSize;
	}
}
