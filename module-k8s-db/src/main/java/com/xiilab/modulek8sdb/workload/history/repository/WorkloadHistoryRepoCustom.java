package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

public interface WorkloadHistoryRepoCustom {

	WorkloadEntity findByWorkspaceNameRecently(String workspace, String username);

	List<WorkloadEntity> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList, WorkloadType workloadType,
		WorkloadSortCondition sortCondition);

	List<WorkloadEntity> getExpiredTimeWorkloadList();

	Page<WorkloadEntity> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType, String searchName,
		String userId,
		List<String> pinResourceNameList, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest,
		WorkloadStatus workloadStatus);

	Page<WorkloadEntity> getAdminWorkloadList(String workspaceName, WorkloadType workloadType, String searchName, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest, WorkloadStatus workloadStatus);

    List<WorkloadEntity> getWorkloadUsedList();
}
