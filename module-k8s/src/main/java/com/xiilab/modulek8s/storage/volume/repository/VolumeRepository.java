package com.xiilab.modulek8s.storage.volume.repository;

import java.util.List;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

public interface VolumeRepository {
	void createVolume(CreateDTO createDTO);

	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageType(String workspaceMetaName, StorageType storageType);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO);

	void deleteVolumeByMetaName(DeleteVolumeDTO deleteVolumeDTO);

	List<PageVolumeResDTO> findVolumesWithPagination(String workspaceMetaName, String option, String keyword);

	List<PageVolumeResDTO> findVolumes(String option, String keyword);

	VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName);
}
