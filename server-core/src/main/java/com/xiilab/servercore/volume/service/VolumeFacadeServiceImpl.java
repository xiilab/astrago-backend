package com.xiilab.servercore.volume.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;
import com.xiilab.servercore.volume.dto.ModifyVolumeReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VolumeFacadeServiceImpl implements VolumeFacadeService {
	private final StorageModuleService storageModuleService;

	@Override
	public void createVolume(CreateVolumeReqDTO requestDTO, UserDTO.UserInfo userInfoDTO) {
		//키클락 유저 정보 넣어줘야함
		requestDTO.setUserInfo(userInfoDTO.getId(), userInfoDTO.getUserName(), userInfoDTO.getUserFullName());
		storageModuleService.createVolume(requestDTO.toModuleDto());
	}

	@Override
	public List<PageVolumeResDTO> findVolumes(SearchCondition searchCondition) {
		FindVolumeDTO findVolumeDTO = FindVolumeDTO.builder()
			.option(searchCondition.getOption())
			.keyword(searchCondition.getKeyword())
			.build();
		return storageModuleService.findVolumes(findVolumeDTO);
	}

	@Override
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName) {
		return storageModuleService.findVolumeByMetaName(volumeMetaName);
	}

	@Override
	public void deleteVolumeByMetaName(String volumeMetaName) {
		storageModuleService.deleteVolumeByMetaName(volumeMetaName);
	}

	@Override
	public void modifyVolume(ModifyVolumeReqDTO modifyVolumeReqDTO, String volumeMetaName) {
		modifyVolumeReqDTO.setVolumeMetaName(volumeMetaName);
		storageModuleService.modifyVolume(modifyVolumeReqDTO.toModuleDto());
	}
}
