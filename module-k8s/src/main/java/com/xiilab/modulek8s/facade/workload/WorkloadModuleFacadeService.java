package com.xiilab.modulek8s.facade.workload;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;

public interface WorkloadModuleFacadeService {
	/**
	 * 워크로드 - 잡 생성
	 * @param moduleCreateWorkloadReqDTO
	 * @return
	 */
	CreateJobResDTO createJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	/**
	 * 배치 워크로드 조회
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	ModuleBatchJobResDTO getBatchWorkload(String workSpaceName, String workloadName);

	/**
	 * 인터렉티브 워크로드 조회
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	ModuleInteractiveJobResDTO getInteractiveWorkload(String workSpaceName, String workloadName);

	void deleteBatchHobWorkload(String workSpaceName, String workloadName);

	void deleteInteractiveJobWorkload(String workSpaceName, String workloadName);

	List<ModuleWorkloadResDTO> getWorkloadList(String workSpaceName);

	ModuleWorkloadResDTO getUserRecentlyWorkload(String workspaceName, String username);

	LogWatch watchLogByWorkload(String workspaceId, String workloadId);

	Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType);

	String getWorkloadLogByWorkloadName(String workspace, String workload, WorkloadType type);

	CreateLocalDatasetResDTO createLocalDataset(CreateLocalDatasetDTO createLocalDatasetDTO);

	WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id);

	void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedDataset(Long datasetId);

	void deleteLocalDataset(DeleteLocalDatasetDTO deleteLocalDatasetDTO);

	CreateLocalModelResDTO createLocalModel(CreateLocalModelDTO createDto);

	WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id);

	void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedModel(Long modelId);

	void deleteLocalModel(DeleteLocalModelDTO deleteLocalModelDTO);
}
