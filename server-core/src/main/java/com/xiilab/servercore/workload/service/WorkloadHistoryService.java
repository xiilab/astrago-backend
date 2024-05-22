package com.xiilab.servercore.workload.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadHistoryReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.servercore.workload.dto.response.OverViewWorkloadResDTO;

public interface WorkloadHistoryService {
	List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName,
		Boolean isCreatedByMe, String userId);

	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName,
		Boolean isCreatedByMe, String userId);

	ModuleWorkloadResDTO getWorkloadHistoryById(long id);

	FindWorkloadResDTO.WorkloadDetail getWorkloadInfoByResourceName(String workspaceName, String workloadResourceName,
		UserDTO.UserInfo userInfoDTO);

	void deleteWorkloadHistory(long id, UserDTO.UserInfo userInfoDTO);

	void saveWorkloadHistory(WorkloadHistoryReqDTO.CreateWorkloadHistory createWorkloadHistory);

	void editWorkloadHistory(WorkloadUpdateDTO workloadUpdateDTO);

	ModuleWorkloadResDTO findByWorkspaceAndRecently(String workspaceName, String username);

	List<JobEntity> getWorkloadByResourceName(String workspaceResourceName);

	void deleteWorkload(String workspaceName);

	List<JobEntity> getWorkloadByResourceNameAndStatus(String workspaceResourceName, WorkloadStatus workloadStatus);

	List<ModuleWorkloadResDTO> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList, WorkloadType workloadType,
		WorkloadSortCondition sortCondition);

	OverViewWorkloadResDTO<ModuleWorkloadResDTO> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType, String searchName, Boolean isCreatedByMe, String id,
		List<String> pinResourceNameList, WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest);

	List<WorkloadResDTO.WorkloadReportDTO> getWorkloadsByWorkspaceIdsAndBetweenCreatedAt(List<String> workspaceIds, LocalDate startDate, LocalDate endDate);
}
