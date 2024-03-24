package com.xiilab.modulek8s.facade.workspace;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.K8SResourceResDTO;
import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.service.ResourceQuotaService;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceModuleFacadeServiceImpl implements WorkspaceModuleFacadeService {
	private final WorkspaceService workspaceService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ResourceQuotaService resourceQuotaService;

	@Override
	public WorkspaceDTO.ResponseDTO createWorkspace(CreateWorkspaceDTO createWorkspaceDTO) {
		//워크스페이스 생성
		WorkspaceDTO.ResponseDTO workspace = workspaceService.createWorkspace(new WorkspaceDTO.RequestDTO(
			createWorkspaceDTO.getName(),
			createWorkspaceDTO.getDescription(),
			LocalDateTime.now(),
			createWorkspaceDTO.getCreatorId(),
			createWorkspaceDTO.getCreatorUserName(),
			createWorkspaceDTO.getCreatorFullName()
		));

		//워크스페이스 resource quota 생성
		resourceQuotaService.createResourceQuotas(
			ResourceQuotaReqDTO.builder()
				.name(createWorkspaceDTO.getName())
				.namespace(workspace.getResourceName())
				.description(createWorkspaceDTO.getDescription())
				.creatorId(createWorkspaceDTO.getCreatorId())
				.creatorUserName(createWorkspaceDTO.getCreatorUserName())
				.creatorFullName(createWorkspaceDTO.getCreatorFullName())
				.reqCPU(createWorkspaceDTO.getReqCPU())
				.reqGPU(createWorkspaceDTO.getReqGPU())
				.reqMEM(createWorkspaceDTO.getReqMEM())
				.build());

		// role 생성

		return workspace;
	}

	@Override
	public List<WorkspaceDTO.ResponseDTO> getWorkspaceList() {
		return workspaceService.getWorkspaceList();
	}

	@Override
	public void updateWorkspaceInfoByName(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO) {
		workspaceService.updateWorkspace(workspaceName, updateDTO);
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName) {
		workspaceService.deleteWorkspaceByName(workspaceName);
	}

	@Override
	public void updateWorkspaceResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq) {
		resourceQuotaService.updateResourceQuota(workspace, cpuReq, memReq, gpuReq);
	}

	@Override
	public WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceName) {
		WorkspaceDTO.ResponseDTO workspace = workspaceService.getWorkspaceByName(workspaceName);
		List<ModuleWorkloadResDTO> workloadList = workloadModuleFacadeService.getWorkloadList(
			workspace.getResourceName());
		ResourceQuotaResDTO resourceQuotas = resourceQuotaService.getResourceQuotas(workspaceName);
		return new WorkspaceTotalDTO(workspace, resourceQuotas, workloadList);
	}

	@Override
	public ResourceQuotaResDTO getWorkspaceResourceQuota(String workspaceResourceName) {
		return resourceQuotaService.getResourceQuotas(workspaceResourceName);
	}

	@Override
	public List<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition) {
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceService.getWorkspaceList();
		if (!StringUtils.isBlank(searchCondition)) {
			workspaceList = workspaceList
				.stream()
				.filter(workspace -> workspace.getName().contains(searchCondition))
				.toList();
		}
		Map<String, ResourceQuotaResDTO> map = resourceQuotaService.getResourceQuotasList().stream().collect(
			Collectors.toMap(K8SResourceResDTO::getName, quota -> quota));

		return workspaceList.stream().map(workspace -> {
			if (map.containsKey(workspace.getResourceName())) {
				ResourceQuotaResDTO resourceQuotaResDTO = map.get(workspace.getResourceName());
				return new WorkspaceDTO.AdminResponseDTO(workspace, resourceQuotaResDTO);
			} else {
				return new WorkspaceDTO.AdminResponseDTO(workspace);
			}
		}).toList();
	}

	@Override
	public void editWorkspaceRole(String workspaceResourceName) {
		workspaceService.editWorkspaceRole(workspaceResourceName);
	}

	@Override
	public void createPodAnnotationsRoleBinding(String workspaceResourceName) {
		workspaceService.createPodAnnotationsRoleBinding(workspaceResourceName);
	}
}
