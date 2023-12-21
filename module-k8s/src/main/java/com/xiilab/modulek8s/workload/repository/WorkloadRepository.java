package com.xiilab.modulek8s.workload.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.vo.JobVO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.vo.WorkloadVO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
@Repository
public interface WorkloadRepository {
	/**
	 * 워크로드 - 배치 잡 타입 생성
	 *
	 * @param jobVO
	 * @return the response DTO containing the details of the created batch job workload
	 */
	JobResDTO createBatchJobWorkload(JobVO jobVO);
	/**
	 * 워크로드 - IDE 잡 타입 생성
	 *
	 * @param workloadVO the workload request DTO containing the necessary information to create the interactive job workload
	 * @return the response DTO containing the details of the created interactive job workload
	 */
	JobResDTO createInteractiveJobWorkload(JobVO jobVO);
}
