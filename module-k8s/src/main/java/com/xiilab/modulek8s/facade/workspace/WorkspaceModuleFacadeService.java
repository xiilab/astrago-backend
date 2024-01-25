package com.xiilab.modulek8s.facade.workspace;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;

public interface WorkspaceModuleFacadeService {
	WorkspaceDTO.ResponseDTO createWorkspace(CreateWorkspaceDTO applicationForm);
	List<WorkspaceDTO.ResponseDTO> getWorkspaceList();
	void deleteWorkspaceByName(String workspaceName);
	WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceName);
}
