package com.xiilab.modulek8s.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;

public interface WorkspaceService {
	WorkspaceDTO.ResponseDTO createWorkspace(WorkspaceDTO.RequestDTO workspaceReqDTO);

	WorkspaceDTO.ResponseDTO getWorkspaceByName(String workspaceName);

	List<WorkspaceDTO.ResponseDTO> getWorkspaceList();

	void updateWorkspace(String workspaceId, WorkspaceDTO.RequestDTO workspaceReqDTO);

	void deleteWorkspaceByName(String workspaceName);
}
