package com.xiilab.servercore.alert.systemalert.service;

import java.util.List;

import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertMappingDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.alert.systemalert.dto.request.ModifyWorkspaceAlertMapping;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindAdminAlertMappingResDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

public interface AlertService {
	Long saveSystemAlert(SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO);
	FindSystemAlertResDTO.SystemAlertDetail getSystemAlertById(Long id);
	FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String loginUserId, SystemAlertReqDTO.FindSearchCondition findSearchCondition);
	// 시스템 알림 읽기여부(N -> Y)로 변경
	void readSystemAlert(Long id);
	void deleteSystemAlertById(Long id);
	// ADMIN 계정 알림설정 초기값 설정
	void initializeAdminAlertMappingSettings(String adminId);
	// Admin Alert 매핑 값 조회
	FindAdminAlertMappingResDTO.AdminAlertMappings findAdminAlertMappings(String adminId);
	void deleteAdminAlertMappings(String adminId);
	// Admin Alert 매핑 값 수정
	void saveAdminAlertMapping(String adminId, List<SystemAlertReqDTO.SaveAdminAlertMappings> saveAdminAlertMappings);
	public List<WorkspaceAlertMappingDTO> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String workspaceResourceName, UserDTO.UserInfo userInfoDTO);

	void modifyWorkspaceAlertMapping(String alertId, String workspaceResourceName, ModifyWorkspaceAlertMapping modifyWorkspaceAlertMapping,
		UserDTO.UserInfo userInfoDTO);

	void readSystemAlerts(UserDTO.UserInfo userInfoDTO, String alertRole);
}
