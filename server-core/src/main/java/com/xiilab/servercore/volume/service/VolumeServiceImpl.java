package com.xiilab.servercore.volume.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.StorageModuleService;
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
}
