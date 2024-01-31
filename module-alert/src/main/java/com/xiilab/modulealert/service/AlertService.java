package com.xiilab.modulealert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.AlertDTO;

@Service
public interface AlertService {
	AlertDTO.ResponseDTO sendAlert(AlertDTO alertDTO);
	List<AlertDTO.ResponseDTO> getAlertList(String recipientId);
	void readAlert(long id);
	void deleteAlert(long id);
}
