package com.xiilab.serverbatch.informer.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.event.UserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
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
	private final WorkspaceAlertService workspaceAlertService;
	private final WorkspaceAlertMappingRepository workspaceAlertMappingRepository;
	@Value("${spring.mail.username}")
	private String adminEmailAddr;
	private final ApplicationEventPublisher publisher;

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
	}

}
