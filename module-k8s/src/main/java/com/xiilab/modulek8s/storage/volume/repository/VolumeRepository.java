package com.xiilab.modulek8s.storage.volume.repository;

import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

public interface VolumeRepository {
	void createVolume(CreateDTO createDTO);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO);

	void deleteVolumeByMetaName(DeleteVolumeDTO deleteVolumeDTO);
}
