package com.xiilab.servercore.volume.service;

import java.util.List;

import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.volume.dto.CreateVolumeReqDTO;
import com.xiilab.servercore.volume.dto.ModifyVolumeReqDTO;

public interface VolumeFacadeService {
	void createVolume(CreateVolumeReqDTO requestDTO, UserInfoDTO userInfoDTO);

	List<PageVolumeResDTO> findVolumes(SearchCondition searchCondition);

	VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName);

	void deleteVolumeByMetaName(String volumeMetaName);

	void modifyVolume(ModifyVolumeReqDTO modifyVolumeReqDTO, String volumeMetaName);
}
