package com.xiilab.modulek8s.facade.workspace;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;

public interface WorkspaceModuleFacadeService {
	WorkspaceDTO.ResponseDTO createWorkspace(CreateWorkspaceDTO applicationForm);
	List<WorkspaceDTO.ResponseDTO> getWorkspaceList();
	void updateWorkspaceInfoByName(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO);
	void deleteWorkspaceByName(String workspaceName);
	void updateWorkspaceResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq);
	WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceName);
	ResourceQuotaResDTO getWorkspaceResourceQuota(String workspaceResourceName);
	List<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition);
	void editWorkspaceRole(String workspaceResourceName);
	void createPodAnnotationsRoleBinding(String workspaceResourceName);
}
