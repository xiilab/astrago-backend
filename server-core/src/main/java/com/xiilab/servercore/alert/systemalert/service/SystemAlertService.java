package com.xiilab.servercore.alert.systemalert.service;

import java.util.List;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;

public interface SystemAlertService {
	void sendAlert(SystemAlertDTO systemAlertDTO);
	SystemAlertDTO.ResponseDTO getAlertById(long id);
	List<SystemAlertDTO.ResponseDTO> getAlertListByUserId(String recipientId);
	void readAlert(long id);
	void deleteAlertById(long id);
}
