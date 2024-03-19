package com.xiilab.servercore.alert.systemalert.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.alert.event.UserAlertEvent;
import com.xiilab.modulecommon.alert.event.WorkspaceAlertMappingDeleteEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener {
	private final AlertRepository alertRepository;
	private final SystemAlertRepository systemAlertRepository;
	private final AdminAlertMappingRepository adminAlertMappingRepository;
	private final WorkspaceAlertMappingRepository workspaceAlertMappingRepository;
	private final UserRepository userRepository;
	private final MailService mailService;
	private final WorkspaceAlertService workspaceAlertService;
	@Value("${spring.mail.username}")
	private String adminEmailAddr;
	private final ApplicationEventPublisher publisher;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleAdminAlertEvent(AdminAlertEvent adminAlertEvent) {
		log.info("관리자[{}] 알림 발송!", adminAlertEvent.title());
		try {
			String REG_USER_ID = "SYSTEM";
			String REG_USER_NAME = "시스템";
			RegUser regUser = new RegUser(adminAlertEvent.senderId() != null? adminAlertEvent.senderId() : REG_USER_ID,
				adminAlertEvent.senderUserName() != null? adminAlertEvent.senderUserName() : REG_USER_NAME,
				adminAlertEvent.senderUserRealName() != null? adminAlertEvent.senderUserRealName() : REG_USER_NAME);

			// AlertRole, Alert 이름으로 ID 조회
			AlertEntity findAlert = alertRepository.findByAlertNameAndAlertRole(adminAlertEvent.alertName().getName(), AlertRole.ADMIN).orElseThrow();

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
						.title(adminAlertEvent.title())
						.message(adminAlertEvent.message())
						.recipientId(findUser.getId())
						.senderId(adminAlertEvent.senderId() != null? adminAlertEvent.senderId() : REG_USER_ID)
						.systemAlertType(findAlert.getAlertType())
						.systemAlertEventType(findAlert.getSystemAlertEventType())
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
	public void handleUserAlertEvent(UserAlertEvent userAlertEvent) {
		// AlertRole, Alert 이름으로 ID 조회
		AlertEntity findAlert = alertRepository.findByAlertNameAndAlertRole(userAlertEvent.alertName().getName(), userAlertEvent.alertRole())
			.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_ALERT));
		List<WorkspaceAlertMappingEntity> workspaceAlertMappingEntities = workspaceAlertMappingRepository.getWorkspaceAlertMappingByAlertId(
			findAlert.getAlertId(), userAlertEvent.workspaceResourceName());

		for (WorkspaceAlertMappingEntity mappingEntity : workspaceAlertMappingEntities) {
			UserDTO.UserInfo findUser = userRepository.getUserById(mappingEntity.getUserId());
			if (mappingEntity.getSystemAlertStatus() == AlertStatus.ON) {
				// save 로직 추가
				SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
					.title(userAlertEvent.title())
					.message(userAlertEvent.message())
					.recipientId(findUser.getEmail())
					.senderId(adminEmailAddr)
					.systemAlertType(findAlert.getAlertType())
					.systemAlertEventType(findAlert.getSystemAlertEventType())
					.readYN(ReadYN.N)
					.build();
				systemAlertRepository.save(saveSystemAlert);
			}
			if (mappingEntity.getEmailAlertStatus() == AlertStatus.ON) {
				// 메일 발송 로직 추가
				mailService.sendMail(MailDTO.builder()
					.title(userAlertEvent.mailTitle())
					.content(userAlertEvent.message())
					.receiverEmail(findUser.getEmail())
					.build());
			}
		}
		if(userAlertEvent.alertName() == AlertName.USER_WORKSPACE_DELETE){
			WorkspaceAlertMappingDeleteEvent workspaceAlertMappingDeleteEvent = new WorkspaceAlertMappingDeleteEvent(
				userAlertEvent.workspaceResourceName());
			publisher.publishEvent(workspaceAlertMappingDeleteEvent);
		}
	}
	@Async
	@EventListener
	public void handleWorkspaceAlertMappingDeleteEvent(WorkspaceAlertMappingDeleteEvent workspaceAlertMappingDeleteEvent) {
		workspaceAlertService.deleteWorkspaceAlertMappingByWorkspaceName(workspaceAlertMappingDeleteEvent.workspaceResourceName());
	}
}
