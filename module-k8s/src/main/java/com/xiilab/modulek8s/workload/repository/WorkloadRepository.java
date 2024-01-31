package com.xiilab.modulek8s.workload.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListenable;

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

	void createConnectTestDeployment(ConnectTestDTO connectTestDTO);

	boolean testConnectPodIsAvailable(String connectTestLabelName, String namespace);

	void deleteConnectTestDeployment(String deploymentName, String namespace);

	void editAstragoDeployment(EditAstragoDeployment editAstragoDeployment);

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
	List<ModuleBatchJobResDTO> getBatchJobWorkloadList(String workSpaceName);

	/**
	 * interactive job workload list 조회
	 *
	 * @param workSpaceName
	 * @return
	 */
	List<ModuleInteractiveJobResDTO> getInteractiveJobWorkloadList(String workSpaceName);

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

	ExecListenable connectBatchJobTerminal(String workspaceName, String workloadName);
	ExecListenable connectInteractiveJobTerminal(String workspaceName, String workloadName);

	Pod getBatchJobPod(String workspaceName, String workloadName);
	Pod getInteractiveJobPod(String workspaceName, String workloadName);



	List<WorkloadResDTO.UsingDatasetDTO> workloadsUsingDataset(Long id);
}
