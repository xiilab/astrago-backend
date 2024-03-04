package com.xiilab.servercore.workload.service;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadHistoryReqDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadHistoryResDTO;

public interface WorkloadHistoryService {
	List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName, String userId);
	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName, String userId);
	ModuleWorkloadResDTO getWorkloadHistoryById(long id);
	WorkloadHistoryResDTO.FindWorkload getWorkloadInfoByResourceName(String workspaceName, String workloadResourceName);
	// ModuleWorkloadResDTO getWorkloadHistoryByWorkloadId(long id);
	void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO);
	void saveWorkloadHistory(WorkloadHistoryReqDTO.CreateWorkloadHistory createWorkloadHistory);
}
