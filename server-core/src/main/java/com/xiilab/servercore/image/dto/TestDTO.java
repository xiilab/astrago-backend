package com.xiilab.servercore.image.dto;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
	private WorkloadType workloadType;
	private ImageType imageType;
	private Integer pageNo;
	private Integer pageSize;

	// public FindSearchCondition(WorkloadType workloadType, ImageType imageType, Integer pageNo, Integer pageSize) {
	// 	this.workloadType = workloadType;
	// 	this.imageType = imageType;
	// 	this.pageNo = pageNo;
	// 	this.pageSize = pageSize;
	// }
}
