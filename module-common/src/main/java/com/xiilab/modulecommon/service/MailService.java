package com.xiilab.modulecommon.service;

public interface MailService {
	void sendMail(String title, String content, String receiverEmail, String senderEmail, String senderUserName);
}
