package com.xiilab.modulek8s.storage.facade;

import com.xiilab.modulek8s.storage.facade.dto.CreateVolumeReqDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;

public interface StorageModuleService {
	void createVolume(CreateVolumeReqDTO requestDTO);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);
}
