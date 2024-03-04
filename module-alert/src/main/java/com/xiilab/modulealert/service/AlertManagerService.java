package com.xiilab.modulealert.service;

import java.util.List;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.dto.AlertManagerReceiveDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;

public interface AlertManagerService {
	AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO );
	void deleteAlertManagerById(long id);
	AlertManagerDTO.ResponseDTO getAlertManagerById(long id);
	void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO);
	List<AlertManagerDTO.ResponseDTO> getAlertManagerList();
	void receiveAlert(String alertData);
	List<AlertManagerReceiveDTO.ResponseDTO> getAlertManagerReceiveList(UserInfoDTO userInfoDTO);
}
