package com.xiilab.modulek8s.workload.service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;

public interface WorkloadService {
	JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);
}
