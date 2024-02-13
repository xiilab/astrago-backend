package com.xiilab.servercore.workload.service;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;

public interface WorkloadHistoryService {
	List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName, String userId);
	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName, String userId);
}
