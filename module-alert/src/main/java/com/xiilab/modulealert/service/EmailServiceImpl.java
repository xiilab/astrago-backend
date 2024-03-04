package com.xiilab.modulealert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.AlertManagerDTO;
import com.xiilab.modulealert.dto.AlertManagerReceiveDTO;

@Service
public class EmailServiceImpl implements EmailService{

	@Override
	public void sendEmail(AlertManagerDTO.ResponseDTO alertManagerDTO, List<AlertManagerReceiveDTO.ReceiveDTO> alertReceiveDTOListDTO) {


	}
}
