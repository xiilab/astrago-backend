package com.xiilab.modulealert.service;

import com.xiilab.modulealert.dto.AlertSetDTO;

public interface AlertSetService {
	void saveAlertSet(String workspaceName);
	AlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);
	AlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, AlertSetDTO alertSetDTO);
	void deleteAlert(String workspaceName);
}
