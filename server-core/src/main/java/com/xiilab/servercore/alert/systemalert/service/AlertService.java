package com.xiilab.servercore.alert.systemalert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.ModifyWorkspaceAlertMapping;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindAdminAlertMappingResDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

public interface AlertService {
	Long saveSystemAlert(SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO);
	FindSystemAlertResDTO.SystemAlertDetail getSystemAlertById(Long id);
	FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String recipientId, SystemAlertType systemAlertType, Pageable pageable);
	// 시스템 알림 읽기여부(N -> Y)로 변경
	void readSystemAlert(Long id);
	void deleteSystemAlertById(Long id);
	// ADMIN 계정 알림설정 초기값 설정
	void initializeAdminAlertMappingSettings(String adminId);
	// TODO 여기부터 진행
	// Admin Alert 매핑 값 조회
	FindAdminAlertMappingResDTO.AdminAlertMappings findAdminAlertMappings(String adminId);
	// Admin Alert 매핑 값 수정
	void saveAdminAlertMapping(String adminId, List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings);
	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String workspaceResourceName, UserInfoDTO userInfoDTO);

	void modifyWorkspaceAlertMapping(String alertId, String workspaceResourceName, ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping,
		UserInfoDTO userInfoDTO);
}
