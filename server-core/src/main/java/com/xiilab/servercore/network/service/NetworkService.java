package com.xiilab.servercore.network.service;

import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.servercore.network.dto.ModifyNetworkDTO;

public interface NetworkService {
	NetworkCloseYN getNetworkStatus();

	void modifyNetworkStatus(ModifyNetworkDTO modifyNetworkDTO);
}
