package com.xiilab.servercore.provisioner.service;

import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;

public interface ProvisionerFacadeService {
	List<ProvisionerResDTO> findProvisioners();

	void installProvisioner(InstallProvisioner installProvisioner);

	void unInstallProvisioner(StorageType storageType);

	List<PluginDTO.ResponseDTO> getPluginList();

	void installPlugin(String type, PluginDTO.DellUnityDTO dellUnityDTO, UserDTO.UserInfo userInfoDTO);

	void uninstallPlugin(String id, UserDTO.UserInfo userInfoDTO);
}
