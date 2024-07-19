package com.xiilab.servercore.workload.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workspace.dto.RecentlyWorkloadDTO;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.OverViewWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadSummaryDTO;

public interface WorkloadHistoryService {
	WorkloadSummaryDTO getWorkloadHistoryById(long id);

	FindWorkloadResDTO getWorkloadInfoByResourceName(String workspaceName, String workloadResourceName,
		UserDTO.UserInfo userInfoDTO);

	void deleteWorkloadHistory(long id, UserDTO.UserInfo userInfoDTO);

	void editWorkloadHistory(WorkloadUpdateDTO workloadUpdateDTO);

	RecentlyWorkloadDTO findByWorkspaceAndRecently(String workspaceName, String username);

	List<WorkloadEntity> getWorkloadByResourceName(String workspaceResourceName);

	void deleteWorkload(String workspaceName);

	List<WorkloadEntity> getWorkloadByResourceNameAndStatus(String workspaceResourceName,
		WorkloadStatus workloadStatus);

	List<WorkloadSummaryDTO> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList,
		WorkloadType workloadType,
		WorkloadSortCondition sortCondition);

	OverViewWorkloadResDTO<WorkloadSummaryDTO> getOverViewWorkloadList(String workspaceName,
		WorkloadType workloadType, String searchName, Boolean isCreatedByMe, String id,
		List<String> pinResourceNameList, WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition,
		PageRequest pageRequest);

	OverViewWorkloadResDTO<WorkloadSummaryDTO> getAdminWorkloadList(String workspaceName, WorkloadType workloadType, String searchName, Boolean isCreatedByMe, WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest);

	List<WorkloadEntity> getWorkloadHistoryByUsingDivisionGPU(String workspaceResourceName);
}
