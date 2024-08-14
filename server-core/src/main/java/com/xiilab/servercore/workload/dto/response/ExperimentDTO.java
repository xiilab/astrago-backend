package com.xiilab.servercore.workload.dto.response;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentDTO {
	private String id;
	private String workloadName;
	private String workloadResourceName;
	private String workspaceName;
	private WorkloadStatus status;
	private WorkloadType type;
	private String username;
	private boolean isView;

	public ExperimentDTO(ExperimentQueryResult experimentQueryResult) {
		this.id = experimentQueryResult.getId();
		this.workloadName = experimentQueryResult.getWorkloadName();
		this.workloadResourceName = experimentQueryResult.getWorkloadResourceName();
		this.workspaceName = experimentQueryResult.getWorkspaceName();
		this.status = experimentQueryResult.getStatus();
		this.type = experimentQueryResult.getType();
		this.username = experimentQueryResult.getUsername();
		this.isView = experimentQueryResult.isView();
	}
}
