package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.List;

import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadType;

public interface WorkloadHistoryRepoCusotm {
	List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType);
}
