package com.xiilab.servercore.workload.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OverViewWorkloadResDTO<T> {
	private long totalSize;
	private List<T> content;
}
