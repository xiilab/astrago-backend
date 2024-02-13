package com.xiilab.servercore.workload.service;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;

public interface WorkloadHistoryService {
	List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName, String userId);
	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName, String userId);
	ModuleWorkloadResDTO getWorkloadHistoryById(long id);
	void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO);
}
