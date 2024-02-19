package com.xiilab.modulek8s.workload.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadModuleServiceImpl implements WorkloadModuleService {
	private final WorkloadRepository workloadRepository;

	public ModuleJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return workloadRepository.createBatchJobWorkload(moduleCreateWorkloadReqDTO.toBatchJobVO());
	}

	@Override
	public ModuleJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return workloadRepository.createInteractiveJobWorkload(moduleCreateWorkloadReqDTO.toInteractiveJobVO());
	}

	@Override
	public void createConnectTestDeployment(ConnectTestDTO connectTestDTO) {
		workloadRepository.createConnectTestDeployment(connectTestDTO);
	}

	@Override
	public boolean isAvailableTestConnectPod(String connectTestLabelName, String namespace) {
		return workloadRepository.testConnectPodIsAvailable(connectTestLabelName, namespace);
	}

	@Override
	public void deleteConnectTestDeployment(String deploymentName, String namespace) {
		workloadRepository.deleteConnectTestDeployment(deploymentName, namespace);
	}

	@Override
	public void editAstragoDeployment(EditAstragoDeployment editAstragoDeployment) {
		workloadRepository.editAstragoDeployment(editAstragoDeployment);
	}

	@Override
	public ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByCondition(String workspaceName, String userId) {
		if (workspaceName != null) {
			return workloadRepository.getBatchWorkloadListByWorkspaceName(workspaceName);
		} else if (userId != null) {
			return workloadRepository.getBatchWorkloadListByCreator(userId);
		} else {
			throw new IllegalArgumentException("workspace, creatorId 둘 중 하나의 조건을 입력해주세요");
		}
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByCondition(String workspaceName, String userId) {
		if (workspaceName != null) {
			return workloadRepository.getInteractiveWorkloadListByWorkspace(workspaceName);
		} else if (userId != null) {
			return workloadRepository.getInteractiveWorkloadByCreator(userId);
		} else {
			throw new IllegalArgumentException("workspace, creatorId 둘 중 하나의 조건을 입력해주세요");
		}
	}

	@Override
	public String deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.deleteBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public String deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public ExecListenable connectWorkloadTerminal(String workloadName, String workspaceName,
		WorkloadType workloadType) {
		if (workloadType == WorkloadType.INTERACTIVE) {
			return workloadRepository.connectInteractiveJobTerminal(workspaceName, workloadName);
		} else if (workloadType == WorkloadType.BATCH) {
			return workloadRepository.connectBatchJobTerminal(workspaceName, workloadName);
		} else {
			return null;
		}
	}

	@Override
	public Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType) {
		if (workloadType == WorkloadType.INTERACTIVE) {
			return workloadRepository.getInteractiveJobPod(workspaceName, workloadName);
		} else if (workloadType == WorkloadType.BATCH) {
			return workloadRepository.getBatchJobPod(workspaceName, workloadName);
		} else {
			return null;
		}
	}

	@Override
	public List<WorkloadResDTO.UsingDatasetDTO> workloadsUsingDataset(Long id) {
		return workloadRepository.workloadsUsingDataset(id);
	}

	@Override
	public void createDatasetDeployment(CreateDatasetDeployment createDeployment) {
		workloadRepository.createDatasetDeployment(createDeployment);
	}

	@Override
	public void createModelDeployment(CreateModelDeployment createDeployment) {
		workloadRepository.createModelDeployment(createDeployment);
	}

	@Override
	public void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO) {
		workloadRepository.modifyLocalDatasetDeployment(modifyLocalDatasetDeploymentDTO);
	}

	@Override
	public boolean isUsedDataset(Long datasetId) {
		return workloadRepository.isUsedDataset(datasetId);
	}

	@Override
	public void deleteDeploymentByResourceName(String deploymentName, String namespace) {
		workloadRepository.deleteDeploymentByResourceName(deploymentName, namespace);
	}

	@Override
	public List<WorkloadResDTO.UsingModelDTO> workloadsUsingModel(Long id) {
		return workloadRepository.workloadsUsingModel(id);
	}

	@Override
	public void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalModelDeploymentDTO) {
		workloadRepository.modifyLocalModelDeployment(modifyLocalModelDeploymentDTO);
	}

	@Override
	public boolean isUsedModel(Long modelId) {
		return workloadRepository.isUsedModel(modelId);
	}

	@Override
	public DirectoryDTO getDirectoryDTOListInWorkloadContainer(String workloadName, String workspaceName,
		WorkloadType workloadType, String path) throws IOException {
		Pod pod = getJobPod(workspaceName, workloadName, workloadType);
		String podName = pod.getMetadata().getName();
		List<String> fileListInWorkloadContainer = workloadRepository.getFileListInWorkloadContainer(
			podName, workspaceName, path);
		DirectoryDTO directoryDTO = DirectoryDTO.convertRawString().rawStringList(fileListInWorkloadContainer).build();
		if (!CollectionUtils.isEmpty(directoryDTO.getChildren())) {
			List<DirectoryDTO.ChildrenDTO> children = directoryDTO.getChildren();
			for (DirectoryDTO.ChildrenDTO child : children) {
				if (child.getType() == FileType.D) {
					int directoryFileCount = workloadRepository.getDirectoryFileCount(podName, workspaceName, child.getPath());
					child.updateFileCount(String.valueOf(directoryFileCount));
				}
			}
		}
		return directoryDTO;
	}

}
