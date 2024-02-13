package com.xiilab.servercore.pin.service;

import java.util.Set;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.enumeration.PinType;

public interface PinService {
	Set<String> getUserWorkspacePinList(String userId);

	Set<String> getUserWorkloadPinList(String userId);
	void createPin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO);
	void deletePin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO);
}
