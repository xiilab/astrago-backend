package com.xiilab.modulek8sdb.alert.systemalert.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.enums.AlertSendType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceAlertService {
	private final WorkspaceAlertMappingRepository workspaceAlertMappingRepository;
	private final AlertRepository alertRepository;

	//workspace 생성, 초대 시 워크스페이스 최초 알림 설정 정보 저장
	@Transactional
	public void initWorkspaceAlertMapping(AlertRole alertRole, String userId, String workspaceResourceName) {
		//1. TB_ALERT에서 관련 알림 엔티티 조회(workspace, workload)
		List<AlertEntity> alerts = alertRepository.getWorkspaceAlertsByOwnerRole();
		//2. 해당 엔티티를 기준으로 TB_WORKSPACE_ALERT_MAPPING 인서트
		//owner 권한이면 (생성 시점이면) 모두 on
		if (AlertRole.OWNER == alertRole) {
			for (AlertEntity alert : alerts) {
				WorkspaceAlertMappingEntity workspaceAlertMappingEntity = WorkspaceAlertMappingEntity.builder()
					.alert(alert)
					.workspaceResourceName(workspaceResourceName)
					.userId(userId)
					.systemAlertStatus(AlertStatus.ON)
					.emailAlertStatus(AlertStatus.OFF)
					.build();
				workspaceAlertMappingRepository.save(workspaceAlertMappingEntity);
			}
		}else if(AlertRole.USER == alertRole){ //user권한이면 (초대 시점이면) user 권한 알림만 on 나머지 off
			for (AlertEntity alert : alerts) {
				WorkspaceAlertMappingEntity workspaceAlertMappingEntity = WorkspaceAlertMappingEntity.builder()
					.alert(alert)
					.workspaceResourceName(workspaceResourceName)
					.userId(userId)
					.systemAlertStatus(alert.getAlertRole() == AlertRole.USER ? AlertStatus.ON : AlertStatus.OFF)
					.emailAlertStatus(alert.getAlertRole() == AlertRole.USER ? AlertStatus.ON : AlertStatus.OFF)
					.build();
				workspaceAlertMappingRepository.save(workspaceAlertMappingEntity);
			}
		}
	}

	/**
	 * workspace 멤버 삭제 시 워크스페이스 알림 매핑 삭제
	 * @param alertRole
	 * @param userId
	 * @param workspaceResourceName
	 */
	@Transactional
	public void deleteWorkspaceAlertMappingByUserIdAndWorkspaceName(String userId, String workspaceResourceName){
		workspaceAlertMappingRepository.deleteWorkspaceAlertMappingByUserIdAndWorkspaceName(userId, workspaceResourceName);
	}
	/**
	 * workspace 삭제 시 워크스페이스 알림 매핑 전체 삭제
	 */
	@Transactional
	public void deleteWorkspaceAlertMappingByWorkspaceName(String workspaceResourceName){
		workspaceAlertMappingRepository.deleteWorkspaceAlertMappingByWorkspaceName(workspaceResourceName);
	}

	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String userId, String workspaceResourceName, AlertRole alertRole){
		List<WorkspaceAlertMappingEntity> alerts = workspaceAlertMappingRepository.getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(
			workspaceResourceName, userId, alertRole);

		return alerts.stream().map(WorkspaceAlertMappingDTO::new).toList();
	}

	@Transactional
	public void modifyWorkspaceAlertMapping(String alertId, AlertRole alertRole, AlertSendType alertSendType, AlertStatus alertStatus,
		String userId) {
		WorkspaceAlertMappingEntity workspaceAlertMappingEntity = workspaceAlertMappingRepository.findById(
			Long.valueOf(alertId)).orElseThrow(() -> new RestApiException(
			SystemAlertErrorCode.NOT_FOUND_ALERT));

		if(!workspaceAlertMappingEntity.getUserId().equals(userId) || workspaceAlertMappingEntity.getAlert().getAlertRole() == AlertRole.OWNER && alertRole == AlertRole.USER){
			throw new RestApiException(SystemAlertErrorCode.ALERT_FORBIDDEN);
		}

		workspaceAlertMappingEntity.modifyAlertStatus(alertSendType, alertStatus);
	}
}
