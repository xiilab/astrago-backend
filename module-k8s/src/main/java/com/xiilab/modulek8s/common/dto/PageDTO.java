package com.xiilab.modulek8s.common.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageDTO<T> {
	private int totalSize;
	private int totalPageNum;
	private int currentPage;
	private List<T> content;

	public PageDTO(List<T> list, int pageNum, int pageSize) {
		int startIndex = (pageNum - 1) * pageSize;
		int endIndex = Math.min(pageNum * pageSize, list.size());
		this.totalSize = list.size();
		this.totalPageNum = (int)Math.ceil((double)list.size() / pageSize);
		currentPage = pageNum;
		this.content = list.subList(startIndex, endIndex);
	}
}
