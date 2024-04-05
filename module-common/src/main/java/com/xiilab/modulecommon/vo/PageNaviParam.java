package com.xiilab.modulecommon.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PageNaviParam {
	private String workspaceResourceName;
	private String workloadResourceName;
	private WorkloadType workloadType;
	private String nodeName;
}
