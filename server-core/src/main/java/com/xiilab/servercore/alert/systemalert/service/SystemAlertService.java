package com.xiilab.servercore.alert.systemalert.service;

import org.springframework.data.domain.Pageable;

import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

public interface SystemAlertService {
	Long saveSystemAlert(SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO);
	FindSystemAlertResDTO.SystemAlertDetail getSystemAlertById(Long id);
	FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String recipientId, Pageable pageable);
	void readSystemAlert(Long id);
	void deleteSystemAlertById(Long id);


}
