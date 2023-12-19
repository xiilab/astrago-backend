package com.xiilab.servercore.workspace.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.workspace.dto.DeleteVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyVolumeReqDTO;

public interface WorkspaceService {
	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageType(String workspaceMetaName, StorageType storageType);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName);

	void modifyVolumeByMetaName(ModifyVolumeReqDTO modifyVolumeReqDTO);

	void deleteVolumeByMetaName(DeleteVolumeReqDTO deleteVolumeReqDTO);

	PageResDTO findVolumesWithPagination(String workspaceMetaName, Pageable pageable,
		SearchCondition searchCondition);
}
