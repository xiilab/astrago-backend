package com.xiilab.servercore.volume.service;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;

public interface VolumeService {
	void createVolume(CreateVolumeReqDTO requestDTO, UserInfoDTO userInfoDTO);
}
