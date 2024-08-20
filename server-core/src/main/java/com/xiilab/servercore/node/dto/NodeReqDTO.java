package com.xiilab.servercore.node.dto;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.util.ValidUtils;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;


@Getter
public class NodeReqDTO {
	@Getter
	public static class FindSearchCondition {
		private Integer pageNo;
		private Integer pageSize;
		@Parameter(required = true)
		private GPUType gpuType;
		private Boolean isMigMixed;

		public FindSearchCondition(Integer pageNo, Integer pageSize, GPUType gpuType, Boolean isMigMixed) {
			this.pageNo = !ValidUtils.isNullOrZero(pageNo) ? pageNo - 1 : 0;
			this.pageSize = !ValidUtils.isNullOrZero(pageSize) ? pageSize : 4;
			this.gpuType = gpuType;
			this.isMigMixed = isMigMixed != null && isMigMixed;
		}
	}
}
