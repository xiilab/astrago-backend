package com.xiilab.servercore.alert.systemalert.service;

import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;

public interface WorkspaceAlertSetService {
	void saveAlertSet(String workspaceName);
	WorkspaceAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);
	WorkspaceAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, WorkspaceAlertSetDTO workspaceAlertSetDTO);
	void deleteAlert(String workspaceName);
}
