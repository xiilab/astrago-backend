package com.xiilab.servercore.pin.service;

import java.util.List;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.dto.PinDTO;

public interface PinService {
	List<PinDTO.ResponseDTO> getUserWorkspacePinList(String userId);

	List<PinDTO.ResponseDTO> getUserWorkloadPinList(String userId);

	void createWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);

	void createWorkloadPin(String resourceId, UserInfoDTO userInfoDTO);
	void deleteWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);
	void deleteWorkloadPin(String resourceId, UserInfoDTO userInfoDTO);
}
