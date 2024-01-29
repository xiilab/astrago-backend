package com.xiilab.servercore.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO);

	List<WorkspaceDTO.ResponseDTO> getWorkspaceList(UserInfoDTO userInfoDTO);

	void deleteWorkspaceByName(String workspaceName);

	List<WorkspaceDTO.ResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO);
}
