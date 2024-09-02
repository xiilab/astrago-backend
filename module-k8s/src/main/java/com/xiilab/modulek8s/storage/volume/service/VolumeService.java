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
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.VolumeMount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolumeService {
	private final VolumeRepository volumeRepository;

	public String createVolume(CreateVolumeDTO createVolumeDTO) {
		return volumeRepository.createVolume(createVolumeDTO);
	}

	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
		return volumeRepository.findVolumesByWorkspaceMetaNameAndStorageMetaName(workspaceMetaName, storageMetaName);
	}

	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName) {
		return volumeRepository.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO) {
		volumeRepository.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(deleteVolumeDTO);
	}

	public PageResDTO findVolumesWithPagination(PageFindVolumeDTO pageFindVolumeDTO) {
		Pageable pageable = pageFindVolumeDTO.getPageable();
		SearchCondition searchCondition = pageFindVolumeDTO.getSearchCondition();

		List<PageVolumeResDTO> volumes = volumeRepository.findVolumesWithPagination(
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
		return volumeRepository.findVolumes(option, keyword);
	}

	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName) {
		return volumeRepository.findVolumeByMetaName(volumeMetaName);
	}

	public void deleteVolumeByMetaName(String volumeMetaName) {
		volumeRepository.deleteVolumeByMetaName(volumeMetaName);
	}

	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.modifyVolume(modifyVolumeDTO);
	}

	public void createPV(CreatePV createPV) {
		volumeRepository.createPV(createPV);
	}

	public void createPVC(CreatePVC createPVC) {
		volumeRepository.createPVC(createPVC);
	}

	public void deletePVC(String pvcName, String namespace) {
		volumeRepository.deletePVC(pvcName, namespace);
	}

	public void deletePV(String pvName) {
		volumeRepository.deletePV(pvName);
	}

	public void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		volumeRepository.deleteStorage(deleteStorageReqDTO);
	}

	public List<String> getAstragoVolumes() {
		List<VolumeMount> astragoVolumes = volumeRepository.getAstragoVolumes();
		return astragoVolumes.stream().map(volumeMount -> volumeMount.getName()).toList();
	}

	public void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> missingPVCs) {
		volumeRepository.astragoCoreDeploymentConnectPVC(missingPVCs);
	}

	public PersistentVolumeClaim createIbmPvc(String storageName) {
		return volumeRepository.createIbmPvc(storageName);
	}

	public void deleteIbmPvc(String storageName) {
		volumeRepository.deleteIbmPvc(storageName);
	}

	public void createDellPVC(String pvcNamem, String storageName){
		volumeRepository.createDellPVC(pvcNamem, storageName);
	}

	public void deleteDellStorage(DeleteStorageReqDTO deleteStorageReqDTO){
		volumeRepository.deleteDellStorage(deleteStorageReqDTO);
	}

	public void deleteStorageClass(DeleteStorageReqDTO deleteStorageReqDTO){
		volumeRepository.deleteStorageClass(deleteStorageReqDTO);
	}
}
