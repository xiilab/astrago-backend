package com.xiilab.modulek8s.storage.volume.service;

import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsDTO;

public interface VolumeRepository {
	void createVolume(CreateVolumeDTO createVolumeDTO);

	VolumeWithWorkloadsDTO findVolumeWithWorkloadsByMetaName(String metaName);
}
