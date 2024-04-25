package com.xiilab.servercore.alert.alertmanager.service;

import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerDTO;

public interface AlertManagerFacadeService {
	AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO);

	void deleteAlertManagerById(long id);

	void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO);

	void enableAlertManagerById(long id, boolean enable);
}
