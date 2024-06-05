package com.xiilab.serverbatch.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.xiilab.modulek8sdb.smtp.entity.SmtpEntity;
import com.xiilab.modulek8sdb.smtp.repository.SmtpRepository;

import lombok.RequiredArgsConstructor;

@EnableScheduling
@Configuration
@RequiredArgsConstructor
public class SmtpQuartzConfig{
	private final SmtpRepository smtpRepository;
	@Scheduled( cron = "0 0 6 * * ?")
	public void sendEmail() {
		List<SmtpEntity> smtpEntityList = smtpRepository.findAll();

		smtpEntityList.forEach(SmtpEntity::setSendCount);
	}
}
