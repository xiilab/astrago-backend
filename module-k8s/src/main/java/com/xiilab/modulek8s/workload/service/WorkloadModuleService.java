package com.xiilab.modulek8s.workload.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListenable;

public interface WorkloadModuleService {
	CreateJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO, String workspaceName);

	CreateJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO, String workspaceName);

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

	WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id);

	void createDatasetDeployment(CreateDatasetDeployment createDeployment);

	void createModelDeployment(CreateModelDeployment createDeployment);

	void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedDataset(Long datasetId);

	void deleteDeploymentByResourceName(String deploymentName, String namespace);

	WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id);

	void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedModel(Long modelId);

	DirectoryDTO getDirectoryDTOListInWorkloadContainer(String workloadName, String workspaceName,
		WorkloadType workloadType, String path) throws IOException;

	FileInfoDTO getFileInfoDtoInWorkloadContainer(String workloadName, String workpaceName,
		WorkloadType workloadType, String path) throws IOException;

	Resource downloadFileFromWorkload(String workloadName, String workpspaceName, WorkloadType workloadType,
		String path) throws IOException;

	Resource downloadFolderFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) throws IOException;

	void deleteFileFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType, String path);

	Boolean uploadFileToWorkload(String workloadName, String workspace, WorkloadType workloadType, String path, File file);

	boolean mkdirToWorkload(String workload, String workspace, WorkloadType workloadType, String path);

	List<ModuleWorkloadResDTO> getAstraInteractiveWorkloadList();

	List<ModuleWorkloadResDTO> getAstraBatchWorkloadList();

	int optimizationInteractiveWorkload(List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList);

	List<ModuleWorkloadResDTO> getParentControllerList(List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList);

	void editBatchJob(String workspaceResourceName, String workloadResourceName, String name, String description);

	void editInteractiveJob(String workspaceResourceName, String workloadResourceName, String name, String description);
}
