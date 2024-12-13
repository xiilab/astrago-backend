package com.xiilab.servercore.volume.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceVolumeDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceVolumeDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.volume.dto.VolumeReqDTO;
import com.xiilab.servercore.volume.dto.VolumeResDTO;

public interface VolumeService {
	Long insertAstragoVolume(AstragoVolumeEntity astragoVolumeEntity, List<MultipartFile> files);

	void insertAstragoOutputVolume(AstragoVolumeEntity astragoVolumeEntity, String volumeName,
		String workspaceResourceName, String workloadResourceName);

	VolumeResDTO.ResVolumes getVolumes(RepositorySearchCondition repositorySearchCondition,
		UserDTO.UserInfo userInfoDTO, PageMode pageMode);

	VolumeResDTO.ResVolumeWithStorage getVolumeWithStorage(Long volumeId);

	Long insertLocalVolume(LocalVolumeEntity localVolumeEntity);

	Volume findById(Long volumeId);

	void modifyVolume(VolumeReqDTO.Edit.ModifyVolume modifyVolumeDTO, Long volumeId);

	void deleteVolumeById(Long volumeId);

	void deleteVolumeWorkspaceMappingById(Long volumeId);

	DirectoryDTO getAstragoVolumeFiles(Long volumeId, String filePath);

	void astragoVolumeUploadFile(Long volumeId, String path, List<MultipartFile> files);

	void astragoVolumeDeleteFiles(Long volumeId, VolumeReqDTO.FilePaths reqFilePathDTO);

	DownloadFileResDTO downloadAstragoVolumeFile(Long volumeId, String filePath);

	void compressAstragoVolumeFiles(Long volumeId, VolumeReqDTO.Compress compress);

	void deCompressAstragoVolumeFile(Long volumeId, String filePath);

	void astragoVolumeCreateDirectory(Long volumeId, VolumeReqDTO.FilePath filePath);

	VolumeResDTO.VolumesInWorkspace getVolumeByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO);

	void insertWorkspaceVolume(InsertWorkspaceVolumeDTO insertWorkspaceVolumeDTO);

	void deleteWorkspaceVolume(String workspaceResourceName, Long volumeId, UserDTO.UserInfo userInfoDTO);

	VolumeResDTO.VolumesInWorkspace getVolumesByWorkspaceResourceName(String workspaceResourceName);

	void deleteVolumeWorkloadMapping(Long volumeId);

	void updateWorkspaceVolume(UpdateWorkspaceVolumeDTO updateWorkspaceVolumeDTO, String workspaceResourceName,
		Long volumeId, UserDTO.UserInfo userInfoDTO);

	void deleteVolumeWorkloadMappingByDeployId(Long id);
}
