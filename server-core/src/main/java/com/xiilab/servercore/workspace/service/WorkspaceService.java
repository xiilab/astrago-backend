package com.xiilab.servercore.workspace.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.workspace.dto.DeleteWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyWorkspaceVolumeReqDTO;

public interface WorkspaceService {
	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName, String storageMetaName);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName);

	void modifyVolumeByMetaName(ModifyWorkspaceVolumeReqDTO modifyWorkspaceVolumeReqDTO);

	void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteWorkspaceVolumeReqDTO deleteWorkspaceVolumeReqDTO);

	PageResDTO findVolumesWithPagination(String workspaceMetaName, Pageable pageable,
		SearchCondition searchCondition);
}
