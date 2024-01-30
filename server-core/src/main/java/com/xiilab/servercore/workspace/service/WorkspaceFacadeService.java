package com.xiilab.servercore.workspace.service;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO);

	PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition, int pageNum, UserInfoDTO userInfoDTO);

	void deleteWorkspaceByName(String workspaceName);

	List<WorkspaceDTO.ResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO);
}
