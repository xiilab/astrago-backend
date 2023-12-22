package com.xiilab.modulek8s.workload.service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;

import java.util.List;

public interface WorkloadService {
	JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);

	WorkloadResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO);

	/**
	 * batch job workload 조회
	 *
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	JobResDTO getBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 조회
	 *
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	WorkloadResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName);

	/**
	 * batch job workload list 조회
	 *
	 * @param workSpaceName
	 * @return
	 */
	List<WorkloadResDTO> getBatchJobWorkloadList(String workSpaceName);

	/**
	 * interactive job workload list 조회
	 *
	 * @param workSpaceName
	 * @return
	 */
	List<WorkloadResDTO> getInteractiveJobWorkloadList(String workSpaceName);

	/**
	 * interactive job workload 수정
	 *
	 * @param workloadReqDTO
	 * @return
	 */
	WorkloadResDTO updateInteractiveJobWorkload(CreateWorkloadReqDTO workloadReqDTO);

	/**
	 * batch job workload 삭제
	 *
	 * @param workSpaceName
	 * @param workloadName
	 */
	String deleteBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 삭제
	 *
	 * @param workSpaceName
	 * @param workloadName
	 */
	String deleteInteractiveJobWorkload(String workSpaceName, String workloadName);
}
