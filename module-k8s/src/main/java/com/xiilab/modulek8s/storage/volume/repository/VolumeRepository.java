package com.xiilab.modulek8s.storage.volume.repository;

import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;

public interface VolumeRepository {
	void createVolume(CreateVolumeDTO createVolumeDTO);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void volumeModifyByMetaName(ModifyVolumeDTO modifyVolumeDTO);
}
