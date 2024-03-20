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

import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.enums.WorkspaceRole;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8s.common.dto.Pageable;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AdminAlertMappingRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.AlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.ModifyWorkspaceAlertMapping;
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
		SystemAlertReqDTO.FindSearchCondition findSearchCondition, Pageable pageable) {
		// 각 타입 카운트를 저장할 map
		Map<SystemAlertType, Long> allAlertTypeCountMap = getAllAlertTypeCountMap(loginUserId,
			findSearchCondition.getReadYN() != null ? findSearchCondition.getReadYN() : null,
			findSearchCondition.getSearchStartDate() != null? findSearchCondition.getSearchStartDate() : null,
			findSearchCondition.getSearchEndDate() != null? findSearchCondition.getSearchEndDate() : null);

		// 페이징 처리
		PageRequest pageRequest = null;
		if (pageable != null && !ObjectUtils.isEmpty(pageable.getPageNumber()) && !ObjectUtils.isEmpty(
			pageable.getPageSize())) {
			pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		}

		// 항목별 조회 API
		Page<SystemAlertEntity> systemAlertEntities = systemAlertRepository.findAlerts(loginUserId,
			findSearchCondition.getSystemAlertType() != null ? findSearchCondition.getSystemAlertType() : null,
			findSearchCondition.getReadYN() != null ? findSearchCondition.getReadYN() : null,
			findSearchCondition.getSearchStartDate() != null? findSearchCondition.getSearchStartDate() : null,
			findSearchCondition.getSearchEndDate() != null? findSearchCondition.getSearchEndDate() : null,
			pageRequest);

		return FindSystemAlertResDTO.SystemAlerts.from(systemAlertEntities.getContent(),
			allAlertTypeCountMap.values().stream().mapToLong(Long::longValue).sum(),
			allAlertTypeCountMap.get(SystemAlertType.USER), allAlertTypeCountMap.get(SystemAlertType.WORKSPACE),
			allAlertTypeCountMap.get(SystemAlertType.WORKLOAD), allAlertTypeCountMap.get(SystemAlertType.LICENSE),
			allAlertTypeCountMap.get(SystemAlertType.NODE), allAlertTypeCountMap.get(SystemAlertType.MEMBER),
			allAlertTypeCountMap.get(SystemAlertType.RESOURCE));
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
	public FindAdminAlertMappingResDTO.AdminAlertMappings findAdminAlertMappings(String adminId) {
		List<AlertEntity> adminAlertMappingList = alertRepository.findAdminAlertMappingsByAdminId(adminId,
			AlertRole.ADMIN);
		return FindAdminAlertMappingResDTO.AdminAlertMappings.from(adminAlertMappingList, adminAlertMappingList.size());
	}

	@Override
	public void saveAdminAlertMapping(String adminId,
		List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings) {
		for (SystemAlertReqDTO.SaveAdminAlertMappings saveAdminAlertMapping : saveAdminAlertMappings) {
			// getAdminAlertMappingId 없으면 새로 등록
			if (NumberValidUtils.isNullOrZero(saveAdminAlertMapping.getAdminAlertMappingId())
				&& !NumberValidUtils.isNullOrZero(saveAdminAlertMapping.getAlertId())) {
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
				findAdminAlertMappingEntity.updateAlertMappingEntity(saveAdminAlertMapping.getEmailAlertStatus(),
					saveAdminAlertMapping.getSystemAlertStatus());
				adminAlertMappingRepository.save(findAdminAlertMappingEntity);
			}
		}
	}

	@Override
	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(
		String workspaceResourceName, UserInfoDTO userInfoDTO) {
		boolean accessAuthorityWorkspace = userInfoDTO.isAccessAuthorityWorkspaceNotAdmin(workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if (!accessAuthorityWorkspace) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfoDTO.getWorkspaceAuthority(workspaceResourceName);
		return workspaceAlertService.getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(userInfoDTO.getId(),
			workspaceResourceName, workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER);
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
	public void modifyWorkspaceAlertMapping(String alertId, String workspaceResourceName,
		ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping, UserInfoDTO userInfoDTO) {
		boolean accessAuthorityWorkspace = userInfoDTO.isAccessAuthorityWorkspaceNotAdmin(workspaceResourceName);
		//워크스페이스 접근 권한 없음
		if (!accessAuthorityWorkspace) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_FORBIDDEN);
		}
		WorkspaceRole workspaceAuthority = userInfoDTO.getWorkspaceAuthority(workspaceResourceName);
		AlertRole alertRole = workspaceAuthority == WorkspaceRole.ROLE_OWNER ? AlertRole.OWNER : AlertRole.USER;
		workspaceAlertService.modifyWorkspaceAlertMapping(alertId, alertRole,
			modifyWorkspaceAlertMapping.getAlertSendType(), modifyWorkspaceAlertMapping.getAlertStatus(),
			userInfoDTO.getId());
	}

	private Map<SystemAlertType, Long> getAllAlertTypeCountMap(String loginUserId, ReadYN readYN,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate) {
		Map<SystemAlertType, Long> allAlertTypeCountMap = new HashMap<>();
		SystemAlertType[] values = SystemAlertType.values();

		for (int i = 0; i < values.length; i++) {
			allAlertTypeCountMap.put(values[i], 0L);
		}

		Page<SystemAlertEntity> allSystemAlertEntities = systemAlertRepository.findAlerts(loginUserId, null, readYN,
			searchStartDate, searchEndDate, null);
		for (SystemAlertEntity allSystemAlertEntity : allSystemAlertEntities.getContent()) {
			// 각 알람 타입별로 카운트 증가
			allAlertTypeCountMap.merge(allSystemAlertEntity.getSystemAlertType(), 1L, Long::sum);
		}

		// 항목별 알림 총합

		return allAlertTypeCountMap;
	}
}
