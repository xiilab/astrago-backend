package com.xiilab.servercore.storageclass.service;

import java.util.List;

import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;
import com.xiilab.servercore.storageclass.dto.ModifyStorageClassReqDTO;

public interface StorageClassFacadeService {
	void createStorageClass(CreateStorageClassReqDTO createStorageClassReqDTO, UserInfoDTO userInfoDTO);

	boolean storageClassConnectionTest(String storageType);

	StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName);

	void modifyStorageClass(ModifyStorageClassReqDTO modifyStorageClassReqDTO, String storageClassMetaName);

	void deleteStorageClass(String storageClassMetaName);

	List<StorageClassResDTO> findStorageClasses();

	List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes();

}
