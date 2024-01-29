package com.xiilab.modulek8s.workload.service;

import java.util.List;

import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.client.dsl.ExecListenable;

public interface WorkloadModuleService {
	ModuleBatchJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	ModuleInteractiveJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	void createConnectTestDeployment(ConnectTestDTO connectTestDTO);

	boolean IsAvailableTestConnectPod(String connectTestLabelName, String namespace);

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

	ExecListenable connectWorkloadTerminal(String workloadName, String workspaceName, WorkloadType workloadType);
}
