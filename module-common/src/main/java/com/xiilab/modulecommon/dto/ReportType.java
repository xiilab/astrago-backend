package com.xiilab.modulecommon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ReportType{
	WEEKLY_CLUSTER("주간 클러스터 리포트"),
	MONTHLY_CLUSTER("월간 클러스터 리포트"),
	WEEKLY_SYSTEM("주간 시스템 리포트"),
	MONTHLY_SYSTEM("월간 시스템 리포트")
	;

	private String name;
}
