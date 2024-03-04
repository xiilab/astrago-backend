package com.xiilab.modulealert.service;

import java.util.List;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.dto.AlertManagerReceiveDTO;

public interface EmailService {
	void sendEmail(AlertManagerDTO.ResponseDTO alertManagerDTO, List<AlertManagerReceiveDTO.ReceiveDTO> alertReceiveDTOListDTO);
}
