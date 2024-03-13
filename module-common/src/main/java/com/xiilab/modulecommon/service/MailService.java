package com.xiilab.modulecommon.service;

import com.xiilab.modulecommon.dto.MailDTO;

public interface MailService {
	void sendMail(MailDTO mailDTO);
}
