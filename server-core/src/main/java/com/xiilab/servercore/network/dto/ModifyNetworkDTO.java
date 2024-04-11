package com.xiilab.servercore.network.dto;

import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;

import lombok.Getter;

@Getter
public class ModifyNetworkDTO {
	private NetworkCloseYN networkStatus;

}
