package com.xiilab.servercore.facade.workspace.service;

import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;

public interface WorkspaceServiceFacade {

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);
}
