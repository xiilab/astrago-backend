package com.xiilab.servercore.alert.systemalert.listener;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.repository.UserRepository;
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
	private final ApplicationEventPublisher publisher;
	private final NetworkRepository networkRepository;

	@Async
	@EventListener
	@Transactional
	public void handleAdminAlertEvent(AdminAlertEvent adminAlertEvent) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if(network.getNetworkCloseYN() == NetworkCloseYN.N){
			log.info("관리자[{}] 알림 발송 시작!", adminAlertEvent.title());
			try {
				// 보내는 유저 정보 조회
				RegUser regUser = getRegUser(adminAlertEvent.sendUserId());

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
							.senderId(StringUtils.hasText(adminAlertEvent.sendUserId())? adminAlertEvent.sendUserId() : "SYSTEM")
							.alertType(findAlert.getAlertType())
							.alertEventType(findAlert.getAlertEventType())
							.alertRole(findAdminAlertMappingEntity.getAlert().getAlertRole())
							.readYN(ReadYN.N)
							.regUser(regUser)
							.pageNaviParam(adminAlertEvent.pageNaviParam())
							.build();
						systemAlertRepository.save(saveSystemAlert);
						log.info("관리자[{}] - 시스템 알림 발송 성공!", adminAlertEvent.title());
					}
					if (findAdminAlertMappingEntity.getEmailAlertStatus() == AlertStatus.ON) {
						// 메일 발송 로직 추가
						mailService.sendMail(MailDTO.builder()
							.title(adminAlertEvent.mailTitle())
							.content(adminAlertEvent.message())
							.receiverEmail(findUser.getEmail())
							.build());
						log.info("관리자[{}] - 메일 알림 발송 성공!", adminAlertEvent.title());
					}
				}
			} catch (Exception e) {
				log.error("관리자[{}] 알림 발송 실패!", adminAlertEvent.title());
			}
		}

	}

	@Async
	@EventListener
	@Transactional
	public void handleWorkspaceUserAlertEvent(WorkspaceUserAlertEvent workspaceUserAlertEvent) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if(network.getNetworkCloseYN() == NetworkCloseYN.N) {
			log.info("워크스페이스 유저[{}] 알림 발송 시작!", workspaceUserAlertEvent.title());
			try {
				if (!StringUtils.hasText(workspaceUserAlertEvent.recipientUserId())) {
					throw new IllegalArgumentException();
				}

				// 보내는 유저 정보 조회
				RegUser regUser = getRegUser(workspaceUserAlertEvent.sendUserId());

				// AlertRole, Alert 이름으로 ID 조회
				AlertEntity findAlert = alertRepository.findByAlertNameAndAlertRole(
						workspaceUserAlertEvent.alertName().getName(), workspaceUserAlertEvent.alertRole())
					.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_ALERT));

				// 알림 받는 유저정보 조회
				WorkspaceAlertMappingEntity findWorkspaceAlertMapping = workspaceAlertMappingRepository.findByAlert_AlertIdAndUserIdAndWorkspaceResourceName(
						findAlert.getAlertId(), workspaceUserAlertEvent.recipientUserId(),
						workspaceUserAlertEvent.workspaceResourceName())
					.orElseThrow();
				UserDTO.UserInfo findRecipientUser = userRepository.getUserById(findWorkspaceAlertMapping.getUserId());

				if (findWorkspaceAlertMapping.getSystemAlertStatus() == AlertStatus.ON) {
					// save 로직 추가
					SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
						.title(workspaceUserAlertEvent.title())
						.message(workspaceUserAlertEvent.message())
						.recipientId(findRecipientUser.getId())
						.senderId(StringUtils.hasText(workspaceUserAlertEvent.sendUserId()) ?
							workspaceUserAlertEvent.sendUserId() : "SYSTEM")
						.alertType(findAlert.getAlertType())
						.alertEventType(findAlert.getAlertEventType())
						.alertRole(findAlert.getAlertRole())
						.readYN(ReadYN.N)
						.regUser(regUser)
						.pageNaviParam(workspaceUserAlertEvent.pageNaviParam())
						.build();
					systemAlertRepository.save(saveSystemAlert);
				}
				if (findWorkspaceAlertMapping.getEmailAlertStatus() == AlertStatus.ON) {
					// 메일 발송 로직 추가
					mailService.sendMail(MailDTO.builder()
						.title(workspaceUserAlertEvent.mailTitle())
						.content(workspaceUserAlertEvent.message())
						.receiverEmail(findRecipientUser.getEmail())
						.build());
				}
				if (workspaceUserAlertEvent.alertName() == AlertName.USER_WORKSPACE_DELETE) {
					WorkspaceAlertMappingDeleteEvent workspaceAlertMappingDeleteEvent = new WorkspaceAlertMappingDeleteEvent(
						workspaceUserAlertEvent.workspaceResourceName());
					publisher.publishEvent(workspaceAlertMappingDeleteEvent);
				}
				log.info("워크스페이스 유저[{}] 알림 발송 성공", workspaceUserAlertEvent.title());
			} catch (Exception e) {
				log.error("워크스페이스 유저[{}] 알림 발송 실패!", workspaceUserAlertEvent.title());
			}
		}
	}

	@Async
	@EventListener
	@Transactional
	public void handleUserAlertEvent(UserAlertEvent userAlertEvent) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		if(network.getNetworkCloseYN() == NetworkCloseYN.N) {
			UserDTO.UserInfo findUser = userRepository.getUserById(userAlertEvent.userId());
			// AlertRole, Alert 이름으로 ID 조회
			if (!ObjectUtils.isEmpty(userAlertEvent.alertName())) {
				UserDTO.UserInfo findSendUser = userRepository.getUserById(userAlertEvent.userId());
				RegUser regUser = new RegUser("SYSTEM", "SYSTEM", "SYSTEM");
				if (!ObjectUtils.isEmpty(findSendUser)) {
					regUser = new RegUser(findSendUser.getId(), findSendUser.getUserName(),
						findSendUser.getLastName() + findSendUser.getFirstName());
				}

				// AlertRole, Alert 이름으로 ID 조회
				AlertEntity findAlert = alertRepository.findByAlertNameAndAlertRole(
					userAlertEvent.alertName().getName(),
					AlertRole.USER).orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_ALERT));

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
					.regUser(regUser)
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

	@Async
	@EventListener
	public void handleWorkspaceAlertMappingDeleteEvent(
		WorkspaceAlertMappingDeleteEvent workspaceAlertMappingDeleteEvent) {
		workspaceAlertService.deleteWorkspaceAlertMappingByWorkspaceName(
			workspaceAlertMappingDeleteEvent.workspaceResourceName());
	}

	private RegUser getRegUser(String sendUserId) {
		RegUser regUser = new RegUser("SYSTEM", "SYSTEM", "SYSTEM");
		if (!ObjectUtils.isEmpty(sendUserId)) {
			UserDTO.UserInfo findSendUser = userRepository.getUserById(sendUserId);
			if (!ObjectUtils.isEmpty(findSendUser)) {
				regUser = new RegUser(findSendUser.getId(), findSendUser.getUserName(), findSendUser.getLastName() + findSendUser.getFirstName());
			}
		}

		return regUser;
	}
}
