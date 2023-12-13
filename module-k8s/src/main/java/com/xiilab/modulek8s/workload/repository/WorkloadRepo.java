package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.workload.dto.JobReqDTO;
import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadReq;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;
import org.springframework.stereotype.Repository;

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

	/**
	 * batch job workload 조회
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	JobResDTO getBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 조회
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	WorkloadRes getInteractiveJobWorkload(String workSpaceName, String workloadName);

	/**
	 * batch job workload 삭제
	 * @param workSpaceName
	 * @param workloadName
	 */
	void deleteBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 삭제
	 * @param workSpaceName
	 * @param workloadName
	 */
	void deleteInteractiveJobWorkload(String workSpaceName, String workloadName);
}
