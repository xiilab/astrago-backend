package com.xiilab.modulek8s.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageDTO {
	private int totalSize;
	private int totalPageNum;
	private int currnetPage;
	private Object content;
}
