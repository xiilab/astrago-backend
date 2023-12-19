package com.xiilab.servercore.storageclass.service;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;

public interface StorageClassFacadeService {
	void createStorageClass(CreateStorageClassReqDTO createStorageClassReqDTO, UserInfoDTO userInfoDTO);
}
