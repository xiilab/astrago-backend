package com.xiilab.modulek8s.workload.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateDistributedWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDistributedJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadModuleServiceImpl implements WorkloadModuleService {
	private final WorkloadRepository workloadRepository;
	private final SvcRepository svcRepository;
	private final NodeRepository nodeRepository;

	public CreateJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO,
		String workspaceName) {
		return workloadRepository.createBatchJobWorkload(moduleCreateWorkloadReqDTO.toBatchJobVO(workspaceName));
	}

	@Override
	public CreateJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO,
		String workspaceName) {
		return workloadRepository.createInteractiveJobWorkload(
			moduleCreateWorkloadReqDTO.toInteractiveJobVO(workspaceName));
	}

	@Override
	public CreateJobResDTO createDistributedJobWorkload(
		ModuleCreateDistributedWorkloadReqDTO moduleCreateWorkloadReqDTO,
		String workspaceName) {
		return workloadRepository.createDistributedJobWorkload(
			moduleCreateWorkloadReqDTO.toDistributedJobVO(workspaceName));
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
		ModuleBatchJobResDTO batchJobWorkload = workloadRepository.getBatchJobWorkload(workSpaceName, workloadName);
		updateJopPodStartTime(workSpaceName, workloadName, WorkloadType.BATCH, batchJobWorkload);
		return batchJobWorkload;
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		ModuleInteractiveJobResDTO interactiveJobWorkload = workloadRepository.getInteractiveJobWorkload(workSpaceName,
			workloadName);
		updateJopPodStartTime(workSpaceName, workloadName, WorkloadType.INTERACTIVE, interactiveJobWorkload);
		return interactiveJobWorkload;
	}

	@Override
	public ModuleDistributedJobResDTO getDistributedJobWorkload(String workSpaceName, String workloadName) {
		ModuleDistributedJobResDTO distributedJobResDTO = workloadRepository.getDistributedJobWorkload(workSpaceName,
			workloadName);
		updateJopPodStartTime(workSpaceName, workloadName, WorkloadType.DISTRIBUTED, distributedJobResDTO);
		return distributedJobResDTO;
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadListByCondition(String workspaceName, Boolean isCreatedByMe,
		String userId) {
		List<ModuleBatchJobResDTO> workloadList;
		// 노드 찾기
		ResponseDTO.PageNodeDTO nodeList = nodeRepository.getNodeList(1, 1, null);
		List<ResponseDTO.NodeDTO> nodes = nodeList.getNodes();
		ResponseDTO.NodeDTO nodeDTO = nodes.get(0);

		if (workspaceName != null && ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadList = workloadRepository.getBatchWorkloadListByWorkspaceName(workspaceName);
		} else if (workspaceName != null && !ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadList = workloadRepository.getBatchWorkloadListByWorkspaceResourceNameAndCreator(workspaceName,
				userId);
		} else if (userId != null) {
			workloadList = workloadRepository.getBatchWorkloadListByCreator(userId);
		} else {
			throw new IllegalArgumentException("workspace, creatorId 둘 중 하나의 조건을 입력해주세요");
		}

		// 포트 업데이트
		workloadList.forEach(workload -> {
			ServiceList servicesByResourceName = svcRepository.getServicesByResourceName(
				workload.getWorkspaceResourceName(),
				workload.getResourceName());
			List<io.fabric8.kubernetes.api.model.Service> items = servicesByResourceName.getItems();
			if (!CollectionUtils.isEmpty(items)) {
				io.fabric8.kubernetes.api.model.Service service = items.get(0);
				workload.updatePort(nodeDTO.getIp(), service);
			}

			updateJopPodStartTime(workload.getWorkspaceResourceName(), workload.getResourceName(), WorkloadType.BATCH,
				workload);
		});

		return workloadList;
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByCondition(String workspaceName,
		Boolean isCreatedByMe, String userId) {
		List<ModuleInteractiveJobResDTO> workloadList;
		ResponseDTO.PageNodeDTO nodeList = nodeRepository.getNodeList(1, 1, null);
		List<ResponseDTO.NodeDTO> nodes = nodeList.getNodes();
		ResponseDTO.NodeDTO nodeDTO = nodes.get(0);
		if (workspaceName != null && ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadList = workloadRepository.getInteractiveWorkloadListByWorkspace(workspaceName);
		} else if (workspaceName != null && !ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadList = workloadRepository.getInteractiveWorkloadListByWorkspaceResourceNameAndCreator(workspaceName,
				userId);
		} else if (userId != null) {
			workloadList = workloadRepository.getInteractiveWorkloadByCreator(userId);
		} else {
			throw new IllegalArgumentException("workspace, creatorId 둘 중 하나의 조건을 입력해주세요");
		}
		workloadList.forEach(workload -> {
			// 포트 URL 업데이트
			ServiceList servicesByResourceName = svcRepository.getServicesByResourceName(
				workload.getWorkspaceResourceName(),
				workload.getResourceName());
			List<io.fabric8.kubernetes.api.model.Service> items = servicesByResourceName.getItems();
			if (!CollectionUtils.isEmpty(items)) {
				io.fabric8.kubernetes.api.model.Service service = items.get(0);
				workload.updatePort(nodeDTO.getIp(), service);
			}
			//
			updateJopPodStartTime(workload.getWorkspaceResourceName(), workload.getResourceName(),
				WorkloadType.INTERACTIVE, workload);
		});
		return workloadList;
	}

	private <T extends AbstractModuleWorkloadResDTO> void updateJopPodStartTime(String workspaceResourceName,
		String workloadResourceName, WorkloadType workloadType, T workload) {
		try {
			Pod pod = getJobPod(workspaceResourceName, workloadResourceName, workloadType);
			workload.setStartTime(DataConverterUtil.convertUTCDateToKorDate(pod.getStatus().getStartTime()));
		} catch (Exception e) {
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
	public void deleteDistributedWorkload(String workspaceName, String workloadName) {
		workloadRepository.deleteDistributedWorkload(workspaceName, workloadName);
	}

	@Override
	public ExecListenable connectWorkloadTerminal(String workloadName, String workspaceName,
		WorkloadType workloadType) {
		if (workloadType == WorkloadType.INTERACTIVE) {
			return workloadRepository.connectInteractiveJobTerminal(workspaceName, workloadName);
		} else if (workloadType == WorkloadType.BATCH) {
			return workloadRepository.connectBatchJobTerminal(workspaceName, workloadName);
		} else if (workloadType == WorkloadType.DISTRIBUTED) {
			return workloadRepository.connectDistributeJobTerminal(workspaceName, workloadName);
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
		} else if (workloadType == WorkloadType.DISTRIBUTED) {
			return workloadRepository.getDistributedLauncherPod(workspaceName, workloadName);
		} else {
			return null;
		}
	}

	@Override
	public WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id) {
		return workloadRepository.workloadsUsingDataset(pageNo, pageSize, id);
	}

	@Override
	public WorkloadResDTO.PageUsingVolumeDTO workloadsUsingVolume(Integer pageNo, Integer pageSize, Long id) {
		return workloadRepository.workloadsUsingVolume(pageNo, pageSize, id);
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

	// isUsedVolume
	@Override
	public boolean isUsedVolume(Long volumeId) {
		return workloadRepository.isUsedVolume(volumeId);
	}

	@Override
	public void deleteDeploymentByResourceName(String deploymentName, String namespace) {
		workloadRepository.deleteDeploymentByResourceName(deploymentName, namespace);
	}

	@Override
	public WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id) {
		return workloadRepository.workloadsUsingModel(pageNo, pageSize, id);
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
					int directoryFileCount = workloadRepository.getDirectoryFileCount(podName, workspaceName,
						child.getPath());
					child.updateFileCount(String.valueOf(directoryFileCount));
				}
			}
		}
		return directoryDTO;
	}

	@Override
	public FileInfoDTO getFileInfoDtoInWorkloadContainer(String workloadName, String workspaceName,
		WorkloadType workloadType, String path) throws IOException {
		Pod pod = getJobPod(workspaceName, workloadName, workloadType);
		String podName = pod.getMetadata().getName();
		List<String> fileListInWorkloadContainer = workloadRepository.getFileInfoInWorkloadContainer(podName,
			workspaceName, path);
		return FileInfoDTO.convertRawString().rawString(fileListInWorkloadContainer.get(0)).build();
	}

	@Override
	public Resource downloadFileFromWorkload(String workloadName, String workpspaceName, WorkloadType workloadType,
		String path) throws
		IOException {
		Pod pod = getJobPod(workpspaceName, workloadName, workloadType);
		CopyOrReadable copyOrReadable = workloadRepository.downloadFileFromPod(pod.getMetadata().getName(),
			workpspaceName, path);
		return convertFileStreamToResource(copyOrReadable);
	}

	@Override
	public Resource downloadFolderFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) throws IOException {
		Pod pod = getJobPod(workspaceName, workloadName, workloadType);
		CopyOrReadable copyOrReadable = workloadRepository.downloadFolderFromPod(pod.getMetadata().getName(),
			workspaceName, path);
		return null;
	}

	@Override
	public void deleteFileFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) {
		Pod pod = getJobPod(workspaceName, workloadName, workloadType);
		workloadRepository.deleteFileFromPod(pod.getMetadata().getName(), workspaceName, path);
	}

	@Override
	public Boolean uploadFileToWorkload(String workloadName, String workspace, WorkloadType workloadType, String path,
		File file) {
		Pod jobPod = getJobPod(workspace, workloadName, workloadType);
		Boolean result = workloadRepository.uploadFileToPod(jobPod.getMetadata().getName(),
			jobPod.getMetadata().getNamespace(), path, file);
		log.info("파일 업로드 성공여부 : " + result);
		return result;
	}

	@Override
	public boolean mkdirToWorkload(String workload, String workspace, WorkloadType workloadType, String path) {
		Pod jobPod = getJobPod(workspace, workload, workloadType);
		return workloadRepository.mkdirToPod(jobPod.getMetadata().getName(), workspace, path);
	}

	public Resource convertFileStreamToResource(CopyOrReadable fileStream) throws IOException {
		InputStream inputStream = fileStream.read();
		// InputStream을 바이트 배열로 복사
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		byte[] fileBytes = outputStream.toByteArray();
		return new ByteArrayResource(fileBytes);
	}

	// private Resource convertFolderToZipResource(CopyOrReadable fileStream, String path) throws IOException {
	// 	//파일 존재하는지 체크용
	// 	InputStream inputStream = fileStream.read();
	// 	InputStream inputStream2 = fileStream.read();
	// 	//InputStream을 체크한다.
	// 	if (inputStream.read() == -1) {
	// 		throw new FileNotFoundException("해당 파일이 존재하지 않습니다.");
	// 	}
	// 	ZipInputStream zipInputStream = new ZipInputStream(inputStream2);
	// 	String[] split = path.split("/");
	// 	String s = split[split.length - 1];
	// 	ZipEntry zipEntry;
	// 	while ((zipEntry = zipInputStream.getNextEntry()) != null) {
	// 		if (!zipEntry.getName().startsWith(s)) {
	// 			continue;
	// 		}
	// 		// 파일일 경우 파일 생성 후 데이터 쓰기
	// 		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/Users/hc.park/test.zip", true));
	// 		byte[] buffer = new byte[1024];
	// 		int bytesRead;
	// 		while ((bytesRead = zipInputStream.read(buffer)) != -1) {
	// 			bos.write(buffer, 0, bytesRead);
	// 		}
	// 		bos.close();
	// 	}
	// 	InputStream inputStream3 = new FileInputStream("/Users/hc.park/test.zip");
	// 	ZipInputStream zipInputStream2 = new ZipInputStream(inputStream3);
	// 	return new InputStreamResource(zipInputStream2);
	// }

	@Override
	public List<AbstractModuleWorkloadResDTO> getAstraInteractiveWorkloadList() {
		return workloadRepository.getAstraInteractiveWorkload();
	}

	@Override
	public List<AbstractModuleWorkloadResDTO> getAstraBatchWorkloadList() {
		return workloadRepository.getAstraBatchWorkload();
	}

	@Override
	public int optimizationInteractiveWorkload(List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList) {
		int totalResultCnt = 0;
		for (ResourceOptimizationTargetDTO resourceOptimizationTargetDTO : resourceOptimizationTargetList) {
			boolean parentResource = workloadRepository.optimizationResource(resourceOptimizationTargetDTO.getPodName(),
				resourceOptimizationTargetDTO.getNamespace());
			if (parentResource) {
				totalResultCnt += 1;
			}
		}
		return totalResultCnt;
	}

	@Override
	public List<AbstractModuleWorkloadResDTO> getParentControllerList(
		List<ResourceOptimizationTargetDTO> resourceOptimizationTargetList) {
		return resourceOptimizationTargetList.stream().map(optimizationTarget -> workloadRepository.getParentController(
			optimizationTarget.getPodName(), optimizationTarget.getNamespace())).toList();
	}

	@Override
	public void editBatchJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		workloadRepository.editBatchJob(workspaceResourceName, workloadResourceName, name, description);
	}

	@Override
	public void editInteractiveJob(String workspaceResourceName, String workloadResourceName, String name,
		String description) {
		workloadRepository.editInteractiveJob(workspaceResourceName, workloadResourceName, name, description);
	}

	@Override
	public List<Event> getWorkloadEventList(String workloadName, String workspace, WorkloadType workloadType) {
		return workloadRepository.getWorkloadEventList(workloadName, workspace);
	}

	@Override
	public Map<String, Event> getWorkloadRecentlyEvent(List<String> workloadName, String workspaceName) {
		return workloadRepository.getWorkloadRecentlyEvent(workloadName, workspaceName);
	}

	@Override
	public HasMetadata getJob(String workspaceName, String workloadName, WorkloadType workloadType) {
		if (workloadType == WorkloadType.INTERACTIVE) {
			return workloadRepository.getInteractiveJob(workspaceName, workloadName);
		} else if (workloadType == WorkloadType.BATCH) {
			return workloadRepository.getBatchJob(workspaceName, workloadName);
		} else {
			return null;
		}
	}

	@Override
	public List<Pod> getWorkloadByWorkloadName(String resourceName) {
		return workloadRepository.getWorkloadsByWorkloadName(resourceName);
	}
}
