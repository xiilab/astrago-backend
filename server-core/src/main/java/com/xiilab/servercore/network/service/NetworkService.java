package com.xiilab.servercore.network.service;

import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.servercore.network.dto.ModifyNetworkDTO;
import com.xiilab.servercore.network.dto.PrivateRepositoryUrlDto;

public interface NetworkService {
	NetworkCloseYN getNetworkStatus();

	void modifyNetworkStatus(ModifyNetworkDTO modifyNetworkDTO);

	PrivateRepositoryUrlDto getPrivateRepositoryUrl();

	void modifyPrivateRepositoryUrl(PrivateRepositoryUrlDto privateRepositoryUrlDto);

	void deletePrivateRepositoryUrl();

	void createPrivateRepositoryUrl(PrivateRepositoryUrlDto privateRepositoryUrlDto);
}
