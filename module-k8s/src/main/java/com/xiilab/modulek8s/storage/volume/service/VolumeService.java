package com.xiilab.modulek8s.storage.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolumeService {
	private final VolumeRepository volumeRepository;

	public void createVolume(CreateVolumeDTO createVolumeDTO){
		volumeRepository.createVolume(createVolumeDTO);
	}

	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName) {
		return volumeRepository.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	public void volumeModifyByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.volumeModifyByMetaName(modifyVolumeDTO);
	}
}
