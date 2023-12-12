package com.xiilab.modulek8s.workload.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.dto.JobReqDTO;
import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadReq;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;
@Repository
public interface WorkloadRepo {
	/**
	 * Creates a batch job workload based on the provided workload request DTO.
	 *
	 * @param workloadReqDTO the workload request DTO containing the necessary information to create the batch job workload
	 * @return the response DTO containing the details of the created batch job workload
	 */
	JobResDTO createBatchJobWorkload(JobReqDTO workloadReqDTO);

	/**
	 * Creates an interactive job workload based on the provided workload request DTO.
	 *
	 * @param workloadReqDTO the workload request DTO containing the necessary information to create the interactive job workload
	 * @return the response DTO containing the details of the created interactive job workload
	 */
	WorkloadRes createInteractiveJobWorkload(WorkloadReq workloadReqDTO);
}
