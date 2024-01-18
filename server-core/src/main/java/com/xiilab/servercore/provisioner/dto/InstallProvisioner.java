package com.xiilab.servercore.provisioner.dto;

import com.xiilab.modulek8s.common.enumeration.StorageType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InstallProvisioner {
	private StorageType storageType;

}
