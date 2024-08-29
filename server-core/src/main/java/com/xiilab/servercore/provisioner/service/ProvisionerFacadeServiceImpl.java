package com.xiilab.servercore.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.facade.provisioner.ProvisionerModuleService;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;
import com.xiilab.modulek8sdb.plugin.service.PluginService;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProvisionerFacadeServiceImpl implements ProvisionerFacadeService {
	private final ProvisionerModuleService provisionerModuleService;
	private final PluginService pluginService;

	@Override
	public List<ProvisionerResDTO> findProvisioners() {
		return provisionerModuleService.findProvisioners();
	}

	@Override
	public void installProvisioner(InstallProvisioner installProvisioner) {
		provisionerModuleService.installProvisioner(installProvisioner.getStorageType());
	}

	@Override
	public void unInstallProvisioner(StorageType storageType) {
		provisionerModuleService.unInstallProvisioner(storageType);
	}

	@Override
	public List<PluginDTO.ResponseDTO> getPluginList(){
		return pluginService.getPluginList();
	}

	@Override
	public void installPlugin(String type, PluginDTO.DellUnityDTO dellUnityDTO,  UserDTO.UserInfo userInfoDTO) {
		if(StorageType.DELL_UNITY.name().equals(type)) {
			pluginService.pluginDeleteYN(StorageType.DELL_UNITY, true, userInfoDTO.getId(), userInfoDTO.getUserFullName());
			provisionerModuleService.installDellProvisioner(dellUnityDTO.getArrayId(), dellUnityDTO.getUsername(),
				dellUnityDTO.getPassword(), dellUnityDTO.getEndpoint());
		}
	}

	@Override
	public void uninstallPlugin(String type, UserDTO.UserInfo userInfoDTO) {
		if(StorageType.DELL_UNITY.name().equals(type)) {
			pluginService.pluginDeleteYN(StorageType.DELL_UNITY, false, "", "");
			provisionerModuleService.uninstallDellProvisioner();
		}
	}

}
