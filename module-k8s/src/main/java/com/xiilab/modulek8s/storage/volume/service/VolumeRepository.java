package com.xiilab.modulek8s.storage.volume.service;

import com.xiilab.modulek8s.storage.volume.dto.RequestInsertDTO;

public interface VolumeRepository {
	void createVolume(RequestInsertDTO requestInsertDTO);
}
