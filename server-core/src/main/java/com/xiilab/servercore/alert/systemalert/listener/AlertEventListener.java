package com.xiilab.servercore.alert.systemalert.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertStatus;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;
import com.xiilab.servercore.alert.systemalert.event.AdminAlertEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener {
	private final AlertRepository alertRepository;
	private final SystemAlertRepository systemAlertRepository;
	private final AdminAlertMappingRepository adminAlertMappingRepository;
	private final UserRepository userRepository;
	private final MailService mailService;
	@Value("${spring.mail.username}")
	private String adminEmailAddr;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleAdminAlertEvent(AdminAlertEvent adminAlertEvent) {
		// AlertRole, Alert 이름으로 ID 조회
		AlertEntity findAlert = alertRepository.findByAlertName(adminAlertEvent.alertName().getName())
			.orElseThrow(() -> new RuntimeException("WTF"));
		// ADMIN - ALERT Mapping 엔티티 조회
		List<AdminAlertMappingEntity> findAdminAlertMappingEntities = adminAlertMappingRepository.findByAlert_AlertId(
			findAlert.getAlertId());
		// 관리자 목록 조회해서 반복문
		for (AdminAlertMappingEntity findAdminAlertMappingEntity : findAdminAlertMappingEntities) {
			UserDTO.UserInfo findUser = userRepository.getUserById(findAdminAlertMappingEntity.getAdminId());
			// TODO exception 처리 필요
			if (findAdminAlertMappingEntity.getSystemAlertStatus() == AlertStatus.ON) {
				// save 로직 추가
				SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
					.title(adminAlertEvent.systemAlertMessage().getTitle())
					.message(adminAlertEvent.systemAlertMessage().getMessage())
					.recipientId(findUser.getEmail())
					.senderId(adminEmailAddr)
					.systemAlertType(findAlert.getAlertType())
					.systemAlertEventType(findAlert.getSystemAlertEventType())
					.readYN(ReadYN.N)
					.build();
				systemAlertRepository.save(saveSystemAlert);
			}
			if (findAdminAlertMappingEntity.getEmailAlertStatus() == AlertStatus.ON) {
				// 메일 발송 로직 추가
				mailService.sendMail(MailDTO.builder()
					.title(adminAlertEvent.systemAlertMessage().getTitle())
					.content(adminAlertEvent.systemAlertMessage().getMessage())
					.receiverEmail(findUser.getEmail())
					.build());
			}
		}
	}
}
