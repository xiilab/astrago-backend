package com.xiilab.servercore.pin.service;

import java.util.Set;

import com.xiilab.servercore.common.dto.UserInfoDTO;

public interface PinService {
	Set<String> getUserWorkspacePinList(String userId);

	Set<String> getUserWorkloadPinList(String userId, String workspaceName);

	void createWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);

	void createWorkloadPin(String resourceId, UserInfoDTO userInfoDTO);
	void deleteWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);
	void deleteWorkloadPin(String resourceId, UserInfoDTO userInfoDTO);
}
