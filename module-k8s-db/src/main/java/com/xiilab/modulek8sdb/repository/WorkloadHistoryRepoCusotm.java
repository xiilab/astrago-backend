package com.xiilab.modulek8sdb.repository;

import java.util.List;

import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;

public interface WorkloadHistoryRepoCusotm {
	List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType);
}
