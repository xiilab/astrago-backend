package com.xiilab.modulek8s.workload.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

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
}
