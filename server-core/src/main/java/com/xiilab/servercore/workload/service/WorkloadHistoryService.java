package com.xiilab.servercore.workload.service;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadHistoryReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;

public interface WorkloadHistoryService {
	List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName, String userId);
	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName, String userId);
	ModuleWorkloadResDTO getWorkloadHistoryById(long id);
	FindWorkloadResDTO.WorkloadDetail getWorkloadInfoByResourceName(String workspaceName, String workloadResourceName);
	void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO);
	void saveWorkloadHistory(WorkloadHistoryReqDTO.CreateWorkloadHistory createWorkloadHistory);
	void editWorkloadHistory(WorkloadUpdateDTO workloadUpdateDTO);
	ModuleWorkloadResDTO findByWorkspaceAndRecently(String workspaceName, String username);

	List<JobEntity> getWorkloadByResourceName(String workspaceResourceName);

	void deleteWorkload(String workspaceName);
}
