package com.xiilab.servercore.alert.systemalert.service;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;

public interface SystemAlertSetService {
	SystemAlertSetDTO.ResponseDTO getSystemAlertSet();
	void updateSystemAlertSet(SystemAlertSetDTO systemAlertSetDTO);
}
