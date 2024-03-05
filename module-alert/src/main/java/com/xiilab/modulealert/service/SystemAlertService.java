package com.xiilab.modulealert.service;

import java.util.List;

import com.xiilab.modulealert.dto.SystemAlertDTO;

public interface SystemAlertService {
	void sendAlert(SystemAlertDTO systemAlertDTO);
	List<SystemAlertDTO.ResponseDTOSystem> getAlertListByUserId(String recipientId);
	void readAlert(long id);
	void deleteAlertById(long id);
	SystemAlertDTO.ResponseDTOSystem getAlertById(long id);
}
