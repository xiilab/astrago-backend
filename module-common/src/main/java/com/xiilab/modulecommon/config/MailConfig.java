package com.xiilab.modulecommon.config;

import java.util.Properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.xiilab.modulecommon.dto.SmtpDTO;

@Configuration
public class MailConfig {

	public JavaMailSender javaMailSender(SmtpDTO smtpDTO) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		configureMailSender(mailSender, smtpDTO);
		return mailSender;
	}


	private void configureMailSender(JavaMailSenderImpl javaMailSender, SmtpDTO smtpDTO) {
		javaMailSender.setHost(smtpDTO.getHost());
		javaMailSender.setPort(smtpDTO.getPort());
		javaMailSender.setUsername(smtpDTO.getUsername());
		javaMailSender.setPassword(smtpDTO.getPassword());
		javaMailSender.setProtocol("smtp");

		Properties props = getProps();
		javaMailSender.setJavaMailProperties(props);
	}

	private Properties getProps() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "false");
		return props;
	}
}
