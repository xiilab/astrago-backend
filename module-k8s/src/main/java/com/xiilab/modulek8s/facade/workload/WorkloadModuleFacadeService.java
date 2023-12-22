package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.BatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.InteractiveJobResDTO;

public interface WorkloadModuleFacadeService {
	/**
	 * 워크로드 - 배치잡 생성
	 * @param createWorkloadReqDTO
	 * @return
	 */
	BatchJobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
	InteractiveJobResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
}
