package com.xiilab.modulek8s.storage.volume.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.Pageable;
import com.xiilab.modulek8s.common.dto.SearchCondition;
import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.facade.dto.PageFindVolumeDTO;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.repository.K8sVolumeRepository;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.VolumeMount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class K8sVolumeService {
	private final K8sVolumeRepository k8sVolumeRepository;

	public String createVolume(CreateVolumeDTO createVolumeDTO) {
		return k8sVolumeRepository.createVolume(createVolumeDTO);
	}

	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
		return k8sVolumeRepository.findVolumesByWorkspaceMetaNameAndStorageMetaName(workspaceMetaName, storageMetaName);
	}

	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName) {
		return k8sVolumeRepository.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		k8sVolumeRepository.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO) {
		k8sVolumeRepository.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(deleteVolumeDTO);
	}

	public PageResDTO findVolumesWithPagination(PageFindVolumeDTO pageFindVolumeDTO) {
		Pageable pageable = pageFindVolumeDTO.getPageable();
		SearchCondition searchCondition = pageFindVolumeDTO.getSearchCondition();

		List<PageVolumeResDTO> volumes = k8sVolumeRepository.findVolumesWithPagination(
			pageFindVolumeDTO.getWorkspaceMetaName(), searchCondition.getOption(), searchCondition.getKeyword());
		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();
		int totalSize = volumes.size();

		return PageResDTO.builder()
			.content(volumes)
			.page(pageNumber)
			.size(pageSize)
			.totalCount(totalSize)
			.build();
	}

	public List<PageVolumeResDTO> findVolumes(FindVolumeDTO findVolumeDTO) {
		String option = findVolumeDTO.getSearchCondition().getOption();
		String keyword = findVolumeDTO.getSearchCondition().getKeyword();
		return k8sVolumeRepository.findVolumes(option, keyword);
	}

	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName) {
		return k8sVolumeRepository.findVolumeByMetaName(volumeMetaName);
	}

	public void deleteVolumeByMetaName(String volumeMetaName) {
		k8sVolumeRepository.deleteVolumeByMetaName(volumeMetaName);
	}

	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		k8sVolumeRepository.modifyVolume(modifyVolumeDTO);
	}

	public void createPV(CreatePV createPV) {
		k8sVolumeRepository.createPV(createPV);
	}

	public void createPVC(CreatePVC createPVC) {
		k8sVolumeRepository.createPVC(createPVC);
	}

	public void deletePVC(String pvcName, String namespace) {
		k8sVolumeRepository.deletePVC(pvcName, namespace);
	}

	public void deletePV(String pvName) {
		k8sVolumeRepository.deletePV(pvName);
	}

	public void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		k8sVolumeRepository.deleteStorage(deleteStorageReqDTO);
	}

	public void deleteDellStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		k8sVolumeRepository.deleteDellStorage(deleteStorageReqDTO);
	}

	public List<String> getAstragoVolumes() {
		List<VolumeMount> astragoVolumes = k8sVolumeRepository.getAstragoVolumes();
		return astragoVolumes.stream().map(volumeMount -> volumeMount.getName()).toList();
	}

	public void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> missingPVCs) {
		k8sVolumeRepository.astragoCoreDeploymentConnectPVC(missingPVCs);
	}

	public PersistentVolumeClaim createIbmPvc(String storageName) {
		return k8sVolumeRepository.createIbmPvc(storageName);
	}

	public void deleteIbmPvc(String storageName) {
		k8sVolumeRepository.deleteIbmPvc(storageName);
	}

	public void createDellPVC(String pvcNamem, String storageName){
		k8sVolumeRepository.createDellPVC(pvcNamem, storageName);
	}
}
