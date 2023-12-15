package com.xiilab.servercore.facade.workspace.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.facade.StorageModuleService;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WorkspaceServiceFacadeImpl implements WorkspaceServiceFacade{
	private final StorageModuleService storageModuleService;
	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName) {
		return storageModuleService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}
}
