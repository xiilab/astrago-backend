package com.xiilab.modulecommon.service;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.dto.SmtpDTO;

public interface MailService {
	void sendMail(MailDTO mailDTO);

	boolean sendMail(MailDTO mailDTO, SmtpDTO smtpDTO);
}
