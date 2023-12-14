package com.xiilab.modulek8s.storage.volume.service;

import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;

public interface VolumeRepository {
	void createVolume(CreateVolumeDTO createVolumeDTO);
}
