package com.xiilab.modulek8s.facade.storage;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.facade.dto.PageFindVolumeDTO;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.StorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.storage.StorageClass;

public interface StorageModuleService {
	void createVolume(CreateVolumeDTO requestDTO);

	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName);

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

	List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes();

	StorageResDTO createStorage(CreateStorageReqDTO createStorageReqDTO);

	StorageClass createIbmStorage(String secretName);

	PersistentVolumeClaim createIbmPvc(String storageName);

	void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO);

	void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> mounts);

	StorageResDTO createDELLStorage(CreateStorageReqDTO createStorageReqDTO);


}
