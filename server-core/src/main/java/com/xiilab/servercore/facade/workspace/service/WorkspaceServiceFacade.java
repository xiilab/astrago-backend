package com.xiilab.servercore.facade.workspace.service;

import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.workspace.dto.ModifyVolumeReqDTO;

public interface WorkspaceServiceFacade {

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void volumeModifyByMetaName(ModifyVolumeReqDTO modifyVolumeReqDTO);
}
