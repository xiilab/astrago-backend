package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;

public interface WorkloadModuleFacadeService {
	JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
	JobResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
}
