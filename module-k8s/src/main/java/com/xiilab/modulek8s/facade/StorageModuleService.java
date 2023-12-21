package com.xiilab.modulek8s.facade;

import com.xiilab.modulek8s.facade.dto.*;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

import java.util.List;

public interface StorageModuleService {
	void createVolume(CreateVolumeDTO requestDTO);

	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName, String storageMetaName);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO);

	void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO);

	PageResDTO findVolumesWithPagination(PageFindVolumeDTO pageFindVolumeDTO);

	List<PageVolumeResDTO> findVolumes(FindVolumeDTO findVolumeDTO);

	VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName);

	void deleteVolumeByMetaName(String volumeMetaName);

	void modifyVolume(ModifyVolumeDTO modifyVolumeDTO);

	void createStorageClass(CreateStorageClassDTO createStorageClassDTO);

	boolean storageClassConnectionTest(String storageType);

	StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName);

	void modifyStorageClass(ModifyStorageClassDTO modifyStorageClassDTO);

	void deleteStorageClass(String storageClassMetaName);

	List<StorageClassResDTO> findStorageClasses();
}
