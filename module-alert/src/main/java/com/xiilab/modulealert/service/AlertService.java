package com.xiilab.modulealert.service;

import java.util.List;

import com.xiilab.modulealert.dto.AlertDTO;

public interface AlertService {
	void sendAlert(AlertDTO alertDTO);
	List<AlertDTO.ResponseDTO> getAlertListByUserId(String recipientId);
	void readAlert(long id);
	void deleteAlertById(long id);
	AlertDTO.ResponseDTO getAlertById(long id);
}
