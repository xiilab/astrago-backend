package com.xiilab.modulealert.service;

import com.xiilab.modulealert.dto.AlertManagerDTO;

public interface AlertManagerService {
	AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO );
}
