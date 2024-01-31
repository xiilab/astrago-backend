package com.xiilab.modulek8s.facade.workload;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;

public interface WorkloadModuleFacadeService {
	/**
	 * 워크로드 - 배치잡 생성
	 * @param moduleCreateWorkloadReqDTO
	 * @return
	 */
	ModuleBatchJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	ModuleInteractiveJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	ModuleBatchJobResDTO getBatchWorkload(String workSpaceName, String workloadName);

	ModuleInteractiveJobResDTO getInteractiveWorkload(String workSpaceName, String workloadName);

	void deleteBatchHobWorkload(String workSpaceName, String workloadName);

	void deleteInteractiveJobWorkload(String workSpaceName, String workloadName);

	List<ModuleWorkloadResDTO> getWorkloadList(String workSpaceName);

	ModuleWorkloadResDTO getUserRecentlyWorkload(String workspaceName, String username);

	LogWatch watchLogByWorkload(String workspaceId, String workloadId);

	Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType);

	String getWorkloadLogByWorkloadName(String workspace, String workload, WorkloadType type);
}
