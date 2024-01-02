package com.xiilab.servercore.storageclass.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;
import com.xiilab.servercore.storageclass.dto.ModifyStorageClassReqDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageClassFacadeServiceImpl implements StorageClassFacadeService {
	private final StorageModuleService storageModuleService;

	@Override
	public void createStorageClass(CreateStorageClassReqDTO createStorageClassReqDTO, UserInfoDTO userInfoDTO) {
		createStorageClassReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		storageModuleService.createStorageClass(createStorageClassReqDTO.toModuleDto());
	}

	@Override
	public boolean storageClassConnectionTest(String storageType) {
		return storageModuleService.storageClassConnectionTest(storageType);
	}

	@Override
	public StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName) {
		return storageModuleService.findStorageClassByMetaName(storageClassMetaName);
	}

	@Override
	public void modifyStorageClass(ModifyStorageClassReqDTO modifyStorageClassReqDTO, String storageClassMetaName) {
		modifyStorageClassReqDTO.setStorageClassMetaName(storageClassMetaName);
		storageModuleService.modifyStorageClass(modifyStorageClassReqDTO.toModuleDto());
	}

	@Override
	public void deleteStorageClass(String storageClassMetaName) {
		storageModuleService.deleteStorageClass(storageClassMetaName);
	}

	@Override
	public List<StorageClassResDTO> findStorageClasses() {
		return storageModuleService.findStorageClasses();
	}

	@Override
	public List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes() {
		return storageModuleService.findStorageClassesWithVolumes();
	}
}
