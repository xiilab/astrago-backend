package com.xiilab.servercore.pin.service;

import java.util.Set;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;

public interface PinService {
	Set<String> getUserWorkspacePinList(String userId);
	Set<String> getUserWorkloadPinList(String userId);
	void createPin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO);
	void deletePin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO);
	void deletePin(String resourceName, PinType pinType);
}
