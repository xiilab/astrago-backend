package com.xiilab.serverbatch.informer.listener;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.alert.event.UserAlertEvent;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformerEventListener {
	private final AlertRepository alertRepository;
	private final SystemAlertRepository systemAlertRepository;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final AdminAlertMappingRepository adminAlertMappingRepository;

	@Async
	@EventListener
	@Transactional
	public void handleAdminAlertEvent(AdminAlertEvent adminAlertEvent) {
		log.info("관리자[{}] 알림 발송 시작!", adminAlertEvent.title());
		try {
			// 보내는 유저 정보 조회
			RegUser regUser = new RegUser("SYSTEM", "SYSTEM", "SYSTEM");
			UserDTO.UserInfo findSendUser = userRepository.getUserById(adminAlertEvent.sendUserId());
			if (!ObjectUtils.isEmpty(findSendUser)) {
				regUser = new RegUser(findSendUser.getId(), findSendUser.getUserName(), findSendUser.getLastName() + findSendUser.getFirstName());
			}

			// AlertRole, Alert 이름으로 ID 조회
			AlertEntity findAlert = alertRepository.findByAlertNameAndAlertRole(adminAlertEvent.alertName().getName(),
				AlertRole.ADMIN).orElseThrow();

			// ADMIN ALERT Mapping 엔티티 조회
			List<AdminAlertMappingEntity> findAdminAlertMappingEntities = adminAlertMappingRepository.findByAlert_AlertId(
				findAlert.getAlertId());

			// 관리자 목록 조회해서 반복문
			for (AdminAlertMappingEntity findAdminAlertMappingEntity : findAdminAlertMappingEntities) {
				UserDTO.UserInfo findUser = userRepository.getUserById(findAdminAlertMappingEntity.getAdminId());
				// TODO exception 처리 필요
				if (findAdminAlertMappingEntity.getSystemAlertStatus() == AlertStatus.ON) {
					// save 로직 추가
					SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
						.title(adminAlertEvent.title())
						.message(adminAlertEvent.message())
						.recipientId(findUser.getId())
						.senderId(StringUtils.hasText(findSendUser.getId())? findSendUser.getId() : "SYSTEM")
						.alertType(findAlert.getAlertType())
						.alertEventType(findAlert.getAlertEventType())
						.alertRole(findAdminAlertMappingEntity.getAlert().getAlertRole())
						.readYN(ReadYN.N)
						.regUser(regUser)
						.build();
					systemAlertRepository.save(saveSystemAlert);
				}
				if (findAdminAlertMappingEntity.getEmailAlertStatus() == AlertStatus.ON) {
					// 메일 발송 로직 추가
					mailService.sendMail(MailDTO.builder()
						.title(adminAlertEvent.mailTitle())
						.content(adminAlertEvent.message())
						.receiverEmail(findUser.getEmail())
						.build());
				}
			}
		} catch (Exception e) {
			log.info("관리자[{}] 알림 발송 실패!", adminAlertEvent.title());
		}
	}

	@Async
	@EventListener
	@Transactional
	public void handleUserAlertEvent(UserAlertEvent userAlertEvent) {
		UserDTO.UserInfo findUser = userRepository.getUserById(userAlertEvent.userId());
		// AlertRole, Alert 이름으로 ID 조회
		if (!ObjectUtils.isEmpty(userAlertEvent)) {
			// Alert 이름으로 ID 조회
			AlertEntity findAlert = alertRepository.findByAlertName(userAlertEvent.alertName().getName())
				.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_ALERT));

			// save 로직 추가
			SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
				.title(userAlertEvent.title())
				.message(userAlertEvent.message())
				.recipientId(findUser.getId())
				.senderId(findUser.getId())
				.alertType(findAlert.getAlertType())
				.alertEventType(findAlert.getAlertEventType())
				.alertRole(findAlert.getAlertRole())
				.readYN(ReadYN.N)
				.build();
			systemAlertRepository.save(saveSystemAlert);
		}

		// 메일 발송 로직 추가
		mailService.sendMail(MailDTO.builder()
			.title(userAlertEvent.mailTitle())
			.content(userAlertEvent.message())
			.receiverEmail(findUser.getEmail())
			.build());
	}

}
