package com.xiilab.modulek8sdb.workload.history.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

public interface WorkloadHistoryRepoCustom {
	List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType);

	JobEntity findByWorkspaceNameRecently(String workspace, String username);

	List<JobEntity> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList, WorkloadType workloadType, WorkloadSortCondition sortCondition);

	Page<JobEntity> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType, String searchName, String userId,
		List<String> pinResourceNameList, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest,
		WorkloadStatus workloadStatus);

	List<WorkloadEntity> getWorkloadsByWorkspaceIdsAndBetweenCreatedAt(List<String> workspaceIds, LocalDate startDate, LocalDate endDate);
}
