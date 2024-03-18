package com.xiilab.servercore.alert.systemalert.service;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertStatus;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindAdminAlertMappingResDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

	private final AlertRepository alertRepository;
	private final SystemAlertRepository systemAlertRepository;
	private final AdminAlertMappingRepository adminAlertMappingRepository;
	private final WorkspaceAlertService workspaceAlertService;

	@Override
	public Long saveSystemAlert(SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO) {
		SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
			.title(saveSystemAlertReqDTO.getTitle())
			.message(saveSystemAlertReqDTO.getMessage())
			.recipientId(saveSystemAlertReqDTO.getRecipientId())
			.senderId(saveSystemAlertReqDTO.getSenderId())
			.systemAlertType(saveSystemAlertReqDTO.getSystemAlertType())
			.systemAlertEventType(saveSystemAlertReqDTO.getSystemAlertEventType())
			.build();
		return systemAlertRepository.save(saveSystemAlert).getId();
	}

	@Override
	public FindSystemAlertResDTO.SystemAlertDetail getSystemAlertById(Long id) {
		SystemAlertEntity systemAlertEntity = systemAlertRepository.findById(id)
			.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_SYSTEM_ALERT));
		return FindSystemAlertResDTO.SystemAlertDetail.from(systemAlertEntity);
	}

	@Override
	public FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String recipientId, SystemAlertType systemAlertType,
		Pageable pageable) {
		Page<SystemAlertEntity> systemAlertEntities = systemAlertRepository.findAlerts(recipientId, systemAlertType,
			pageable);
		return FindSystemAlertResDTO.SystemAlerts.from(systemAlertEntities.getContent(),
			systemAlertEntities.getTotalElements());
	}

	@Override
	public void readSystemAlert(Long id) {
		SystemAlertEntity systemAlertEntity = systemAlertRepository.findById(id)
			.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_SYSTEM_ALERT));
		systemAlertEntity.readAlert();
		systemAlertRepository.save(systemAlertEntity);
	}

	@Override
	public void deleteSystemAlertById(Long id) {
		systemAlertRepository.deleteById(id);
	}

	@Override
	public void initializeAdminAlertMappingSettings(String adminId) {
		List<AlertEntity> adminAlertList = alertRepository.findByAlertRole(AlertRole.ADMIN);
		for (AlertEntity alertEntity : adminAlertList) {
			AdminAlertMappingEntity saveAdminAlertMappingEntity = AdminAlertMappingEntity.saveBuilder()
				.adminId(adminId)
				.alert(alertEntity)
				.systemAlertStatus(AlertStatus.OFF)
				.emailAlertStatus(AlertStatus.OFF)
				.build();
			adminAlertMappingRepository.save(saveAdminAlertMappingEntity);
		}
	}

	@Override
	public FindAdminAlertMappingResDTO.AdminAlertMappings findAdminAlertMappings(String adminId) {
		List<AlertEntity> adminAlertMappingList = alertRepository.findAdminAlertMappingsByAdminId(
			adminId);
		return FindAdminAlertMappingResDTO.AdminAlertMappings.from(adminAlertMappingList,
			adminAlertMappingList.size());
	}

	@Override
	public void saveAdminAlertMapping(String adminId, List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings) {
		for (SystemAlertReqDTO.SaveAdminAlertMappings saveAdminAlertMapping : saveAdminAlertMappings) {
			// getAdminAlertMappingId 없으면 새로 등록
			if (NumberValidUtils.isNullOrZero(saveAdminAlertMapping.getAdminAlertMappingId()) &&
				!NumberValidUtils.isNullOrZero(saveAdminAlertMapping.getAlertId())) {
				AlertEntity alertEntity = alertRepository.findById(saveAdminAlertMapping.getAlertId())
					.orElseThrow(() -> new RuntimeException("Hello world!"));
				AdminAlertMappingEntity newAdminAlertMappingEntity = AdminAlertMappingEntity.saveBuilder()
					.alert(alertEntity)
					// .adminId(saveAdminAlertMapping.getAdminId())
					.adminId(adminId)
					.emailAlertStatus(saveAdminAlertMapping.getEmailAlertStatus())
					.systemAlertStatus(saveAdminAlertMapping.getSystemAlertStatus())
					.build();

				adminAlertMappingRepository.save(newAdminAlertMappingEntity);
			} else { // getAdminAlertMappingId 있으면 업데이트
				AdminAlertMappingEntity findAdminAlertMappingEntity = adminAlertMappingRepository.findById(
					saveAdminAlertMapping.getAdminAlertMappingId())
					.orElseThrow(() -> new RuntimeException("Hello world!"));
				findAdminAlertMappingEntity.updateAlertMappingEntity(saveAdminAlertMapping.getEmailAlertStatus(), saveAdminAlertMapping.getSystemAlertStatus());
				adminAlertMappingRepository.save(findAdminAlertMappingEntity);
			}
		}
	}
	@Override
	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String workspaceResourceName, UserInfoDTO userInfoDTO){
		boolean accessAuthorityWorkspace = userInfoDTO.isAccessAuthorityWorkspaceNotAdmin(
			workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if(!accessAuthorityWorkspace){
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfoDTO.getWorkspaceAuthority(workspaceResourceName);
		return workspaceAlertService.getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(userInfoDTO.getId(), workspaceResourceName,
			workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER);
	}

	/**
	 * 워크스페이스 매핑 알림 설정 ON/OFF 수정
	 * @param alertId
	 * @param workspaceResourceName
	 * @param modifyWorkspaceAlertMapping
	 * @param userInfoDTO
	 */
	@Override
	@Transactional
	public void modifyWorkspaceAlertMapping(String alertId, String workspaceResourceName, ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping,
		UserInfoDTO userInfoDTO) {
		boolean accessAuthorityWorkspace = userInfoDTO.isAccessAuthorityWorkspaceNotAdmin(
			workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if(!accessAuthorityWorkspace){
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfoDTO.getWorkspaceAuthority(workspaceResourceName);
		AlertRole alertRole = workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER;
		workspaceAlertService.modifyWorkspaceAlertMapping(alertId, alertRole, modifyWorkspaceAlertMapping.getAlertSendType(),
			modifyWorkspaceAlertMapping.getAlertStatus(), userInfoDTO.getId());
	}
}
