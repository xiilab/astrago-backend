package com.xiilab.modulek8s.workload.service;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListenable;

public interface WorkloadModuleService {
	ModuleJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO, String workspaceName);

	ModuleJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO, String workspaceName);

	void createConnectTestDeployment(ConnectTestDTO connectTestDTO);

	boolean isAvailableTestConnectPod(String connectTestLabelName, String namespace);

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

	List<ModuleBatchJobResDTO> getBatchWorkloadListByCondition(String workspaceName, String userId);

	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByCondition(String workspaceName, String userId);

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

	Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType);

	List<WorkloadResDTO.UsingDatasetDTO> workloadsUsingDataset(Long id);

	void createDatasetDeployment(CreateDatasetDeployment createDeployment);

	void createModelDeployment(CreateModelDeployment createDeployment);

	void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedDataset(Long datasetId);

	void deleteDeploymentByResourceName(String deploymentName, String namespace);

	List<WorkloadResDTO.UsingModelDTO> workloadsUsingModel(Long id);

	void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedModel(Long modelId);

	List<ModuleWorkloadResDTO> getAstraInteractiveWorkloadList();

	List<ModuleWorkloadResDTO> getAstraBatchWorkloadList();

	int optimizationInteractiveWorkload(List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList);

	List<ModuleWorkloadResDTO> getParentControllerList(List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList);
}
