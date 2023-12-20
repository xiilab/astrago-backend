package com.xiilab.servercore.storageclass.service;

import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;

public interface StorageClassFacadeService {
	void createStorageClass(CreateStorageClassReqDTO createStorageClassReqDTO, UserInfoDTO userInfoDTO);

	boolean storageClassConnectionTest(String storageType);

	StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName);
}
