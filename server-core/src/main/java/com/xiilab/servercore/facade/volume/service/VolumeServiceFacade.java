package com.xiilab.servercore.facade.volume.service;

import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;

public interface VolumeServiceFacade {
	void createVolume(CreateVolumeReqDTO requestDTO);
}
