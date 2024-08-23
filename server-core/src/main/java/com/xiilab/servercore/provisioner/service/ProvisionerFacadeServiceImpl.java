package com.xiilab.servercore.provisioner.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.facade.provisioner.ProvisionerModuleService;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;
import com.xiilab.modulek8sdb.plugin.service.PluginService;
import com.xiilab.servercore.provisioner.dto.InstallProvisioner;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProvisionerFacadeServiceImpl implements ProvisionerFacadeService{
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
	public void installPlugin(long id, boolean result){
		pluginService.installPlugin(id, result);
	}

}
