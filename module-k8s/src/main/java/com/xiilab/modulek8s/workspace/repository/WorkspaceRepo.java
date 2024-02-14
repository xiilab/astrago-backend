package com.xiilab.modulek8s.workspace.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceReqVO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceResVO;

@Repository
public interface WorkspaceRepo {
	WorkspaceResVO createWorkSpace(WorkspaceReqVO workspaceReqVO);

	WorkspaceResVO getWorkspaceByName(String name);

	List<WorkspaceResVO> getWorkspaceList();

	WorkspaceDTO.WorkspaceResourceStatus getWorkspaceResourceStatus(String workspaceName);

	void deleteWorkspaceByName(String name);

	void updateWorkspaceInfo(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO);
}

