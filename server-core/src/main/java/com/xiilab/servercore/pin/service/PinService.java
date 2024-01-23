package com.xiilab.servercore.pin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.dto.PinDTO;

@Service
public interface PinService {
	List<PinDTO.ResponseDTO> getUserWorkspacePinList(String userId);

	List<PinDTO.ResponseDTO> getUserWorkloadPinList(String userId);

	void createWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);

	void createWorkloadPin(String resourceId);
	void deleteWorkspacePin(String resourceId, UserInfoDTO userInfoDTO);
	void deleteWorkloadPin(String resourceId, UserInfoDTO userInfoDTO);
}
