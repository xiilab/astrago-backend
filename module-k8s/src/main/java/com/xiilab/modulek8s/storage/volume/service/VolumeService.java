package com.xiilab.modulek8s.storage.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolumeService {
	private final VolumeRepository volumeRepository;

	public void createVolume(CreateDTO createDTO){
		volumeRepository.createVolume(createDTO);
	}

	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName) {
		return volumeRepository.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.modifyVolumeByMetaName(modifyVolumeDTO);
	}
	public void deleteVolumeByMetaName(DeleteVolumeDTO deleteVolumeDTO){
		volumeRepository.deleteVolumeByMetaName(deleteVolumeDTO);
	}
}
