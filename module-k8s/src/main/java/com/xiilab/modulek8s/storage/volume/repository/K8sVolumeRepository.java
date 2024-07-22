package com.xiilab.modulek8s.storage.volume.repository;

import java.util.List;

import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.VolumeMount;

public interface K8sVolumeRepository {
	String createVolume(CreateVolumeDTO createVolumeDTO);

	List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName);

	VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName);

	void createPVC(CreatePVC createPVC);

	void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO);

	void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO);

	List<PageVolumeResDTO> findVolumesWithPagination(String workspaceMetaName, String option, String keyword);

	List<PageVolumeResDTO> findVolumes(String option, String keyword);

	VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName);

	void deleteVolumeByMetaName(String volumeMetaName);

	void modifyVolume(ModifyVolumeDTO modifyVolumeDTO);

	void createPV(CreatePV createPV);

	void deletePVC(String pvcName, String namespace);

	void deletePV(String pvName);

	void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO);

	List<VolumeMount> getAstragoVolumes();

	void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> missingPVC);

	PersistentVolumeClaim createIbmPvc(String storageName);

	void deleteIbmPvc(String storageName);
}
