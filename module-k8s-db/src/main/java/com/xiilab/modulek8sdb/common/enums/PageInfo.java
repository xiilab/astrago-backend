package com.xiilab.modulek8sdb.common.enums;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class PageInfo {
	private Integer pageNo;
	private Integer pageSize;
	public PageInfo(Integer pageNo, Integer pageSize) {
		this.pageNo = pageNo == null ? 1 : pageNo;
		this.pageSize = pageSize == null ? 10 : pageSize;
	}
}
