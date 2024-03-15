package com.xiilab.servercore.alert.alertmanager.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerDTO;
import com.xiilab.modulek8sdb.alert.alertmanager.dto.AlertManagerReceiveDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;

public interface AlertManagerService {
	AlertManagerDTO.ResponseDTO saveAlertManager(AlertManagerDTO.RequestDTO requestDTO );
	void deleteAlertManagerById(long id);
	AlertManagerDTO.ResponseDTO getAlertManagerById(long id);
	void updateAlertManagerById(long id, AlertManagerDTO.RequestDTO requestDTO);
	List<AlertManagerDTO.ResponseDTO> getAlertManagerList();
	void receiveAlert(String alertData);
	Page<AlertManagerReceiveDTO.ResponseDTO> getAlertManagerReceiveList(String categoryType, String startDate, String endDate, String search, UserInfoDTO userInfoDTO, Pageable pageable);
	AlertManagerDTO.ResponseDTO enableAlertManagerById(long id, boolean enable);
	AlertManagerReceiveDTO.ResponseDTO getAlertManagerReceiveByReceiveId(long id);
}
