package com.xiilab.modulek8sdb.workload.history.repository;

import java.time.LocalDate;
import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

public interface WorkloadHistoryRepoCustom {
	List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType);
	JobEntity findByWorkspaceNameRecently(String workspace, String username);

	List<WorkloadEntity> getWorkloadsByWorkspaceIdsAndBetweenCreatedAt(List<String> workspaceIds, LocalDate startDate,
		LocalDate endDate);
}
