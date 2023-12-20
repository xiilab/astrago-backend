package com.xiilab.modulek8s.storage.volume.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.Pageable;
import com.xiilab.modulek8s.common.dto.SearchCondition;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.facade.dto.PageFindVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolumeService {
	private final VolumeRepository volumeRepository;

	public void createVolume(CreateDTO createDTO){
		volumeRepository.createVolume(createDTO);
	}

	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageType(String workspaceMetaName, StorageType storageType){
		return volumeRepository.findVolumesByWorkspaceMetaNameAndStorageType(workspaceMetaName, storageType);
	}
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName) {
		return volumeRepository.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.modifyVolumeByMetaName(modifyVolumeDTO);
	}
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO){
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
		// int startIndex = (pageNumber - 1) * pageSize;
		// int endIndex = Math.min(startIndex + pageSize, totalSize);

		// if (startIndex >= totalSize || endIndex <= startIndex) {
		// 	// 페이지 범위를 벗어나면 빈 리스트 반환
		// 	return PageResDTO.builder()
		// 		.content(null)
		// 		.page(pageNumber)
		// 		.size(pageSize)
		// 		.totalCount(totalSize)
		// 		.build();
		// }
		// List<PageVolumeResDTO> volumeResDTOS = volumes.subList(startIndex, endIndex);

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
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName){
		return volumeRepository.findVolumeByMetaName(volumeMetaName);
	}

	public void deleteVolumeByMetaName(String volumeMetaName) {
		volumeRepository.deleteVolumeByMetaName(volumeMetaName);
	}

	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		volumeRepository.modifyVolume(modifyVolumeDTO);
	}
}
