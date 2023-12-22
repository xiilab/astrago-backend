package com.xiilab.modulek8s.workload.service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.BatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.InteractiveJobResDTO;

public interface WorkloadService {
	BatchJobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
	InteractiveJobResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
}
