package com.xiilab.modulealert.service;

import java.util.List;

import com.xiilab.modulealert.dto.AlertManagerDTO;

public interface AlertManagerService {
	AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO );
	void deleteAlertManagerById(long id);
	AlertManagerDTO.ResponseDTO getAlertManagerById(long id);
	void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO);
	List<AlertManagerDTO.ResponseDTO> getAlertManagerList();
}
