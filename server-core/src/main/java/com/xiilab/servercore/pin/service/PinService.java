package com.xiilab.servercore.pin.service;

import java.util.Set;

import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.moduleuser.dto.UserDTO;

public interface PinService {
	Set<String> getUserWorkspacePinList(String userId);

	Set<String> getUserWorkloadPinList(String userId);

	void createPin(String resourceName, PinType pinType, UserDTO.UserInfo userInfoDTO);

	void deletePin(String resourceName, PinType pinType, UserDTO.UserInfo userInfoDTO);

	void deletePin(String resourceName, PinType pinType);
}
