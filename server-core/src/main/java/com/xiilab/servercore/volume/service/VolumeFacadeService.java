package com.xiilab.servercore.volume.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.volume.dto.VolumeReqDTO;
import com.xiilab.servercore.volume.dto.VolumeResDTO;

public interface VolumeFacadeService {
	void insertAstragoVolume(VolumeReqDTO.Edit.CreateAstragoVolume createAstragoVolumeDTO, List<MultipartFile> files);

	VolumeResDTO.ResVolumeWithStorage getVolume(Long volumeId);

	void insertLocalVolume(VolumeReqDTO.Edit.CreateLocalVolume createLocalVolumeDTO);

	void modifyVolume(VolumeReqDTO.Edit.ModifyVolume modifyVolumeDTO, Long volumeId, UserDTO.UserInfo userInfoDTO);

	void deleteVolume(Long volumeId, UserDTO.UserInfo userInfoDTO);

	DirectoryDTO getLocalVolumeFiles(Long volumeId, String filePath);

	DownloadFileResDTO downloadLocalVolumeFile(Long volumeId, String filePath);

	FileInfoDTO getLocalVolumeFileInfo(Long volumeId, String filePath);

	DownloadFileResDTO getLocalVolumeFile(Long volumeId, String filePath);

	FileInfoDTO getAstragoVolumeFileInfo(Long volumeId, String filePath);

	DownloadFileResDTO getAstragoVolumeFile(Long volumeId, String filePath);

	WorkloadResDTO.PageUsingVolumeDTO getWorkloadsUsingVolume(PageInfo pageInfo, Long volumeId,
		UserDTO.UserInfo userInfoDTO);
}
