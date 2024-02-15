package com.xiilab.modulek8s.workspace.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceVO;

@Repository
public interface WorkspaceRepo {
	WorkspaceVO.ResponseVO createWorkSpace(WorkspaceVO.RequestVO workspaceReqVO);

	WorkspaceVO.ResponseVO getWorkspaceByName(String name);

	List<WorkspaceVO.ResponseVO> getWorkspaceList();

	WorkspaceDTO.WorkspaceResourceStatus getWorkspaceResourceStatus(String workspaceName);

	void deleteWorkspaceByName(String name);

	void updateWorkspaceInfo(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO);
}

