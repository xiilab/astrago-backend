package com.xiilab.servercore.smtp.service;

import java.util.List;

import com.xiilab.modulek8sdb.smtp.dto.SmtpDTO;

public interface SmtpFacadeService {

	void saveSmtp(SmtpDTO.RequestDTO requestDTO);

	List<SmtpDTO.ResponseDTO> getSmtp();

	void deleteSmtpById(long id);

}
