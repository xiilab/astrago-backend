package com.xiilab.servercore.workspace.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.DeleteWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceTotalDTO;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm);

	void deleteWorkspaceByName(String workspaceName);

	List<WorkspaceDTO.ResponseDTO> getWorkspaceList(UserInfoDTO userInfoDTO);

	WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceName);

	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageType(String workspaceMetaName, StorageType storageType);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName);

	void modifyVolumeByMetaName(ModifyWorkspaceVolumeReqDTO modifyWorkspaceVolumeReqDTO);

	void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteWorkspaceVolumeReqDTO deleteWorkspaceVolumeReqDTO);

	PageResDTO findVolumesWithPagination(String workspaceMetaName, Pageable pageable,
		SearchCondition searchCondition);
}
