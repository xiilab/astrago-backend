package com.xiilab.modulealert.service;

import com.xiilab.modulealert.dto.SystemAlertSetDTO;

public interface SystemAlertSetService {
	void saveAlertSet(String workspaceName);
	SystemAlertSetDTO.ResponseDTOSystem getWorkspaceAlertSet(String workspaceName);
	SystemAlertSetDTO.ResponseDTOSystem updateWorkspaceAlertSet(String workspaceName, SystemAlertSetDTO systemAlertSetDTO);
	void deleteAlert(String workspaceName);
}
