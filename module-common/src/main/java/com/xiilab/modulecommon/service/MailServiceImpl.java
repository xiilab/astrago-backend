package com.xiilab.modulecommon.service;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.util.MailUtils;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{
	private final String SYSTEM = "system";
	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String adminEmailAddr;

	public void sendMail(String title, String content, String receiverEmail, String senderEmail, String senderUserName) {

		try{
			MailUtils sendMail = new MailUtils(mailSender);
			sendMail.setSubject(title);
			sendMail.setText(content);
			sendMail.setTo(receiverEmail);
			sendMail.setFrom(senderEmail, senderUserName);
			sendMail.send();
		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new RestApiException(CommonErrorCode.MAIL_SEND_FAILED);
		}
	}
}