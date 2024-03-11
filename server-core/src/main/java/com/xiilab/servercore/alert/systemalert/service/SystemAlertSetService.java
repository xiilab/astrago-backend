package com.xiilab.servercore.alert.systemalert.service;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;

public interface SystemAlertSetService {
	void saveAlertSet(String workspaceName);
	SystemAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);
	SystemAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, SystemAlertSetDTO systemAlertSetDTO);
	void deleteAlert(String workspaceName);
}
