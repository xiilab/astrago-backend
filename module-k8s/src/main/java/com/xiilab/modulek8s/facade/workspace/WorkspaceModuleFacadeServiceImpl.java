package com.xiilab.modulek8s.facade.workspace;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.service.ResourceQuotaService;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;

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
			createWorkspaceDTO.getCreatorUserName(),
			createWorkspaceDTO.getCreatorId()
		));

		//워크스페이스 resource quota 생성
		resourceQuotaService.createResourceQuotas(
			ResourceQuotaReqDTO.builder()
				.name(createWorkspaceDTO.getName())
				.namespace(workspace.getResourceName())
				.description(createWorkspaceDTO.getDescription())
				.creatorId(createWorkspaceDTO.getCreatorId())
				.creatorUserName(createWorkspaceDTO.getCreatorId())
				.reqCPU(createWorkspaceDTO.getReqCPU())
				.reqGPU(createWorkspaceDTO.getReqGPU())
				.reqMEM(createWorkspaceDTO.getReqMEM())
				.reqDisk(createWorkspaceDTO.getReqDisk())
				.build());

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
}
