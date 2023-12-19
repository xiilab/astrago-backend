package com.xiilab.servercore.storageclass.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.StorageModuleService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageClassFacadeServiceImpl implements StorageClassFacadeService{
	private final StorageModuleService storageModuleService;

	@Override
	public void createStorageClass(CreateStorageClassReqDTO createStorageClassReqDTO, UserInfoDTO userInfoDTO) {
		createStorageClassReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		storageModuleService.createStorageClass(createStorageClassReqDTO.toModuleDto());
	}
}
