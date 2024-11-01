package com.xiilab.servercore.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageableUtils {
	public Pageable normalizePageable(Pageable pageable) {
		int pageNumber = pageable.previousOrFirst().getPageNumber();
		int pageSize = pageable.getPageSize() > 0 ? pageable.getPageSize() : 10;
		return PageRequest.of(pageNumber, pageSize);
	}
}
