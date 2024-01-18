package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;

import java.util.List;

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
}
