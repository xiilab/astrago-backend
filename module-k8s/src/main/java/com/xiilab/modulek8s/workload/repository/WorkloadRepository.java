package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.vo.JobVO;
import org.springframework.stereotype.Repository;

import java.util.List;

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
	 * @param jobVO the workload request DTO containing the necessary information to create the interactive job workload
	 * @return the response DTO containing the details of the created interactive job workload
	 */
	JobResDTO createInteractiveJobWorkload(JobVO jobVO);

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
