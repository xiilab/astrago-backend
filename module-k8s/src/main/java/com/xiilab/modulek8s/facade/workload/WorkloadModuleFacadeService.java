package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;

public interface WorkloadModuleFacadeService {
	/**
	 * 워크로드 - 배치잡 생성
	 * @param moduleCreateWorkloadReqDTO
	 * @return
	 */
	ModuleBatchJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);
	ModuleInteractiveJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);
}
