package com.xiilab.servercore.alert.systemalert.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.enums.WorkspaceRole;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.ModifyWorkspaceAlertMapping;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindAdminAlertMappingResDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
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
			.alertType(saveSystemAlertReqDTO.getAlertType())
			.alertEventType(saveSystemAlertReqDTO.getAlertEventType())
			.alertRole(saveSystemAlertReqDTO.getAlertRole())
			.readYN(ReadYN.N)
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
	public FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String loginUserId,
		SystemAlertReqDTO.FindSearchCondition findSearchCondition) {
		// 페이징 처리
		PageRequest pageRequest = null;
		if (!ObjectUtils.isEmpty(findSearchCondition.getPage()) && !ObjectUtils.isEmpty(
			findSearchCondition.getSize())) {
			pageRequest = PageRequest.of(findSearchCondition.getPage(), findSearchCondition.getSize());
		}

		// 각 타입 카운트를 저장할 map
		Map<AlertType, Long> allAlertTypeCountMap = getAllAlertTypeCountMap(loginUserId,
			findSearchCondition.getAlertRole(),
			findSearchCondition.getReadYN(),
			findSearchCondition.getSearchText(),
			findSearchCondition.getSearchStartDate(),
			findSearchCondition.getSearchEndDate());

		// 항목별 조회 API
		Page<SystemAlertEntity> systemAlertEntities = systemAlertRepository.findAlerts(loginUserId,
			findSearchCondition.getAlertType(),
			findSearchCondition.getAlertRole(),
			findSearchCondition.getReadYN(),
			findSearchCondition.getSearchText(),
			findSearchCondition.getSearchStartDate(),
			findSearchCondition.getSearchEndDate(),
			pageRequest);

		return FindSystemAlertResDTO.SystemAlerts.from(systemAlertEntities.getContent(),
			allAlertTypeCountMap.values().stream().mapToLong(Long::longValue).sum(),
			allAlertTypeCountMap.get(AlertType.USER), allAlertTypeCountMap.get(AlertType.WORKSPACE),
			allAlertTypeCountMap.get(AlertType.WORKLOAD), allAlertTypeCountMap.get(AlertType.LICENSE),
			allAlertTypeCountMap.get(AlertType.NODE), allAlertTypeCountMap.get(AlertType.MEMBER),
			allAlertTypeCountMap.get(AlertType.RESOURCE));
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
			Optional<AdminAlertMappingEntity> findAdminAlertMappingEntity = adminAlertMappingRepository.findByAdminIdAndAlert_AlertId(
				adminId, alertEntity.getAlertId());
			// 매핑 엔티티에 없을 때만 초기 값 세팅
			if (findAdminAlertMappingEntity.isEmpty()) {
				AdminAlertMappingEntity saveAdminAlertMappingEntity = AdminAlertMappingEntity.saveBuilder()
					.adminId(adminId)
					.alert(alertEntity)
					.systemAlertStatus(AlertStatus.OFF)
					.emailAlertStatus(AlertStatus.OFF)
					.build();
				adminAlertMappingRepository.save(saveAdminAlertMappingEntity);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FindAdminAlertMappingResDTO.AdminAlertMappings findAdminAlertMappings(String adminId) {
		List<AdminAlertMappingEntity> findAdminAlertMappings = adminAlertMappingRepository.findAdminAlertMappingsByAdminId(
			adminId, AlertRole.ADMIN);

		if (findAdminAlertMappings.isEmpty()) {
			List<AlertEntity> findAlertEntities = alertRepository.findByAlertRole(AlertRole.ADMIN);
			return FindAdminAlertMappingResDTO.AdminAlertMappings.fromDefaultAlerts(findAlertEntities,
				findAlertEntities.size());
		} else {
			return FindAdminAlertMappingResDTO.AdminAlertMappings.fromAdminAlertsMappings(findAdminAlertMappings,
				findAdminAlertMappings.size());
		}
	}

	@Override
	public void deleteAdminAlertMappings(String adminId) {
		adminAlertMappingRepository.deleteByAdminId(adminId);
	}

	@Override
	public void saveAdminAlertMapping(String adminId,
		List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings) {
		for (SystemAlertReqDTO.SaveAdminAlertMappings saveAdminAlertMapping : saveAdminAlertMappings) {
			// getAdminAlertMappingId 없으면 새로 등록
			if (ValidUtils.isNullOrZero(saveAdminAlertMapping.getAdminAlertMappingId())
				&& !ValidUtils.isNullOrZero(saveAdminAlertMapping.getAlertId())) {

				AlertEntity alertEntity = alertRepository.findById(saveAdminAlertMapping.getAlertId())
					.orElseThrow(() -> new RuntimeException("Hello world!"));
				AdminAlertMappingEntity newAdminAlertMappingEntity = AdminAlertMappingEntity.saveBuilder()
					.alert(alertEntity)
					.adminId(adminId)
					.emailAlertStatus(saveAdminAlertMapping.getEmailAlertStatus())
					.systemAlertStatus(saveAdminAlertMapping.getSystemAlertStatus())
					.build();

				adminAlertMappingRepository.save(newAdminAlertMappingEntity);
			} else { // getAdminAlertMappingId 있으면 업데이트
				AdminAlertMappingEntity findAdminAlertMappingEntity = adminAlertMappingRepository.findById(
						saveAdminAlertMapping.getAdminAlertMappingId())
					.orElseThrow(() -> new RuntimeException("Hello world!"));

				findAdminAlertMappingEntity.updateAlertMappingEntity(saveAdminAlertMapping.getEmailAlertStatus(),
					saveAdminAlertMapping.getSystemAlertStatus());
				adminAlertMappingRepository.save(findAdminAlertMappingEntity);
			}
		}
	}

	@Override
	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(
		String workspaceResourceName, UserDTO.UserInfo userInfo) {
		boolean accessAuthorityWorkspace = userInfo.isAccessAuthorityWorkspaceNotAdmin(workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if (!accessAuthorityWorkspace) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfo.getWorkspaceAuthority(workspaceResourceName);
		return workspaceAlertService.getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(userInfo.getId(),
			workspaceResourceName, workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER);
	}

	/**
	 * 워크스페이스 매핑 알림 설정 ON/OFF 수정
	 * @param alertId
	 * @param workspaceResourceName
	 * @param modifyWorkspaceAlertMapping
	 * @param userInfo
	 */
	@Override
	@Transactional
	public void modifyWorkspaceAlertMapping(String alertId, String workspaceResourceName,
		ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping, UserDTO.UserInfo userInfo) {
		boolean accessAuthorityWorkspace = userInfo.isAccessAuthorityWorkspaceNotAdmin(workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if (!accessAuthorityWorkspace) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfo.getWorkspaceAuthority(workspaceResourceName);
		AlertRole alertRole = workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER;
		workspaceAlertService.modifyWorkspaceAlertMapping(alertId, alertRole,
			modifyWorkspaceAlertMapping.getAlertSendType(), modifyWorkspaceAlertMapping.getAlertStatus(),
			userInfo.getId());
	}

	@Override
	@Transactional
	public void readSystemAlerts(UserDTO.UserInfo userInfoDTO, String alertRole) {
		List<SystemAlertEntity> systemAlertEntities = systemAlertRepository.getSystemAlertEntitiesByRecipientIdAndAlertRole(
			userInfoDTO.getId(), AlertRole.valueOf(alertRole));
		systemAlertEntities.forEach(SystemAlertEntity::readAlert);
	}

	private Map<AlertType, Long> getAllAlertTypeCountMap(String loginUserId, AlertRole alertRole, ReadYN readYN, String searchText,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate) {
		Map<AlertType, Long> allAlertTypeCountMap = new HashMap<>();
		AlertType[] values = AlertType.values();

		for (int i = 0; i < values.length; i++) {
			allAlertTypeCountMap.put(values[i], 0L);
		}

		Page<SystemAlertEntity> allSystemAlertEntities = systemAlertRepository.findAlerts(loginUserId, null, alertRole, readYN,
			searchText, searchStartDate, searchEndDate, null);
		for (SystemAlertEntity allSystemAlertEntity : allSystemAlertEntities.getContent()) {
			// 각 알람 타입별로 카운트 증가
			allAlertTypeCountMap.merge(allSystemAlertEntity.getAlertType(), 1L, Long::sum);
		}

		// 항목별 알림 총합

		return allAlertTypeCountMap;
	}
}
