package com.xiilab.modulek8s.common.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageDTO<T> {
	private int totalSize;
	private int totalPageNum;
	private int currentPage;
	private List<T> content;

	/**
	 * 일반 페이지네이션
	 * @param list 페이지네이션 할 list
	 * @param pageNum 조회를 원하는 pageNum
	 * @param pageSize 페이지당 사이즈
	 */
	public PageDTO(List<T> list, int pageNum, int pageSize) {
		int startIndex = (pageNum - 1) * pageSize;
		int endIndex = Math.min(pageNum * pageSize, list.size());
		this.totalSize = list.size();
		this.totalPageNum = (int)Math.ceil((double)list.size() / pageSize);
		currentPage = pageNum;
		this.content = list.subList(startIndex, endIndex);
	}

	/**
	 * pin이 들어간 페이지네이션
	 * @param pinList 핀 리스트
	 * @param normalList 핀을 제외한 일반 리스트
	 * @param pageNum 조회를 원하는 pageNum
	 * @param pageSize 페이지당 사이즈
	 */
	public PageDTO(List<T> pinList, List<T> normalList, int pageNum, int pageSize) {
		List<T> result = new ArrayList<>();
		//pinList가 상단에 고정되므로, 한 페이지의 size - pinList size를 뺀다.
		int normalPageSize = pageSize - pinList.size();
		int startIndex = (pageNum - 1) * pageSize;
		int endIndex = Math.min(pageNum * pageSize, normalList.size());
		int totalPage = (int)Math.ceil(normalList.size() / (double)normalPageSize);
		//normal list
		List<T> subListResult = normalList.subList(startIndex, endIndex);
		result.addAll(pinList);
		result.addAll(subListResult);
		if (totalPage == 0 && CollectionUtils.isEmpty(pinList)) {
			this.content = new ArrayList<>();
		} else {
			if (totalPage < pageNum) {
				//사용자가 더 많은 페이지 인덱스를 입력했을 경우
				throw new IllegalArgumentException("total page size보다 입력한 pageNum이 더 큽니다.");
			}
			this.totalSize = pinList.size() + normalList.size();
			this.totalPageNum = totalPage;
			this.currentPage = pageNum;
			this.content = result;
		}
	}
}
