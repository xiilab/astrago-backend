package com.xiilab.modulek8s.workload.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkloadStatusCnt {
	private int running;
	private int error;
	private int pending;
	private int end;
}
