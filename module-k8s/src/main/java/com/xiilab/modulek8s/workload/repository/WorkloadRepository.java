package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import java.util.List;

@Repository
public interface WorkloadRepository {
	/**
	 * 워크로드 - 배치 잡 타입 생성
	 *
	 * @param batchJobVO
	 * @return BatchJobResDTO
	 */
	ModuleBatchJobResDTO createBatchJobWorkload(BatchJobVO batchJobVO);
	/**
	 * 워크로드 - IDE 잡 타입 생성
	 *
	 * @param interactiveJobVO
	 * @return InteractiveJobResDTO
	 */
	ModuleInteractiveJobResDTO createInteractiveJobWorkload(InteractiveJobVO interactiveJobVO);

    /**
     * batch job workload 조회
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName);

    /**
     * interactive job workload 조회
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName);

    /**
     * batch job workload list 조회
     *
     * @param workSpaceName
     * @return
     */
    List<ModuleWorkloadResDTO> getBatchJobWorkloadList(String workSpaceName);

    /**
     * interactive job workload list 조회
     *
     * @param workSpaceName
     * @return
     */
    List<ModuleWorkloadResDTO> getInteractiveJobWorkloadList(String workSpaceName);

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
