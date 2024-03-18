package com.xiilab.servercore.workload.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadUpdateDTO {
	private String workspaceResourceName;
	private String workloadResourceName;
	private String name;
	private String description;
}
