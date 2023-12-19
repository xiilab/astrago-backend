package com.xiilab.servercore.volume.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.StorageModuleService;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolumeServiceImpl implements VolumeService{
	private final StorageModuleService storageModuleService;

	@Override
	public void createVolume(CreateVolumeReqDTO requestDTO, UserInfoDTO userInfoDTO){
		//키클락 유저 정보 넣어줘야함
		requestDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
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
		VolumeWithStorageResDTO volume = storageModuleService.findVolumeByMetaName(volumeMetaName);
		return volume;
	}
}
