package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;

public interface WorkloadHistoryRepoCustom {
	List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType);

	JobEntity findByWorkspaceNameRecently(String workspace, String username);
}
