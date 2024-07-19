package com.xiilab.servercore.smtp.service;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8sdb.smtp.dto.SmtpDTO;

public interface SmtpFacadeService {

	void saveSmtp(SmtpDTO.RequestDTO requestDTO);

	PageDTO<SmtpDTO.ResponseDTO> getSmtp(int pageNum, int pageSize);

	void deleteSmtpById(long id);

}
