package com.xiilab.servercore.deploy.service;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.deploy.dto.CreateDeployReqDTO;
import com.xiilab.servercore.node.service.NodeFacadeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeployFacadeServiceImpl{

	private final NodeFacadeService nodeFacadeService;
	private final NetworkRepository networkRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final WorkspaceService workspaceService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;

	public void createDeploy(CreateDeployReqDTO createDeployReqDTO, UserDTO.UserInfo userInfoDTO) {
		createDeployReqDTO.setUserInfo(userInfoDTO.getId(), userInfoDTO.getUserName(), userInfoDTO.getUserFullName());

		// MPS 노드 지정
		if (!Objects.isNull(createDeployReqDTO.getGpuType()) && createDeployReqDTO.getGpuType() == GPUType.MPS) {
			if (StringUtils.hasText(createDeployReqDTO.getNodeName())) {
				createDeployReqDTO.setNodeName(getMpsNodeName(createDeployReqDTO.getNodeName()));
			}
		}
		try {
			NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
			// 리소스 초과 알림
			checkAndSendWorkspaceResourceOverAlert(createDeployReqDTO, userInfoDTO);
			//모델 파일 -> triton model repository copy
			//model로 storage 조회

			//triton model 디렉토리 생성

			//pv, pvc 생성
			//triton deployment 생성
			// workloadModuleFacadeService.createDeployWorkload(
			// 	createDeployReqDTO.toModuleDTO(network.getInitContainerURL()));
		} catch (Exception e) {
			log.error(e.toString());
			throw e;
		}

	}

	private void checkAndSendWorkspaceResourceOverAlert(CreateDeployReqDTO createDeployReqDTO,
		UserDTO.UserInfo userInfoDTO) {
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			createDeployReqDTO.getWorkspace());
		// CPU
		float cpuUsed = workspaceResourceStatus.getResourceStatus().getCpuUsed();
		if (cpuUsed != 0.0f) {
			cpuUsed = cpuUsed / 1000.0f;
		}

		// MEM
		float memUsed = workspaceResourceStatus.getResourceStatus().getMemUsed();
		if (memUsed != 0.0f) {
			memUsed = memUsed / 1000.0f;
		}

		boolean isCpuOverResource = isOverResource(cpuUsed,
			createDeployReqDTO.getCpuRequest(),
			workspaceResourceStatus.getResourceStatus().getCpuLimit());
		// GPU
		boolean isGpuOverResource = isOverResource(workspaceResourceStatus.getResourceStatus().getGpuUsed(),
			createDeployReqDTO.getGpuRequest(), workspaceResourceStatus.getResourceStatus().getGpuLimit());

		boolean isMemOverResource = isOverResource(memUsed,
			createDeployReqDTO.getMemRequest(),
			workspaceResourceStatus.getResourceStatus().getMemLimit());

		if (isCpuOverResource || isGpuOverResource || isMemOverResource) {
			AlertMessage workspaceResourceOverAdmin = AlertMessage.WORKSPACE_RESOURCE_OVER_ADMIN;
			String mailTitle = String.format(workspaceResourceOverAdmin.getMailTitle(),
				workspaceResourceStatus.getName());
			String title = workspaceResourceOverAdmin.getTitle();
			String message = String.format(workspaceResourceOverAdmin.getMessage(),
				workspaceResourceStatus.getCreatorFullName(), workspaceResourceStatus.getCreatorUserName(),
				workspaceResourceStatus.getName());

			eventPublisher.publishEvent(
				new AdminAlertEvent(AlertName.ADMIN_WORKSPACE_RESOURCE_OVER, userInfoDTO.getId(), mailTitle, title,
					message,
					PageNaviParam.builder().workspaceResourceName(createDeployReqDTO.getWorkspace()).build(),
					null));

		}
	}

	private boolean isOverResource(float workspaceResourceUsed, float createWorkloadResourceUsed,
		float workspaceResourceLimit) {
		float totalUsed = workspaceResourceUsed + createWorkloadResourceUsed;
		return totalUsed > workspaceResourceLimit;
	}
	private String getMpsNodeName(String nodeNames) {
		String[] splitNodeName = nodeNames.replaceAll(" ", "").split(",");
		if (splitNodeName.length == 1) {
			return splitNodeName[0];
		} else {
			// 할당 가능한 GPU가 가장 많은 노드에 워크로드 생성
			String createWorkloadNodeName = null;
			int maximumAllocatableGPU = Integer.MIN_VALUE;
			for (String nodeName : splitNodeName) {
				ResponseDTO.NodeResourceInfo nodeResourceInfo = nodeFacadeService.getNodeResourceByResourceName(
					nodeName);
				int allocatableGPU = Integer.parseInt(nodeResourceInfo.getAllocatable().getAllocatableGpu());
				if (allocatableGPU > maximumAllocatableGPU) {
					createWorkloadNodeName = nodeName;
					maximumAllocatableGPU = allocatableGPU;
				}
			}
			return createWorkloadNodeName;
		}
	}
}
