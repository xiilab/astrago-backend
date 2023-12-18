package com.xiilab.servercore.facade.workspace.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.StorageModuleService;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.workspace.dto.DeleteVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyVolumeReqDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WorkspaceServiceFacadeImpl implements WorkspaceServiceFacade {
	private final StorageModuleService storageModuleService;


	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageType(String workspaceMetaName, StorageType storageType){
		 return storageModuleService.findVolumesByWorkspaceMetaName(workspaceMetaName, storageType);
	}

	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName) {
		return storageModuleService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	@Override
	public void modifyVolumeByMetaName(ModifyVolumeReqDTO modifyVolumeReqDTO) {
		storageModuleService.modifyVolumeByMetaName(modifyVolumeReqDTO.toModuleDto());
	}

	@Override
	public void deleteVolumeByMetaName(DeleteVolumeReqDTO deleteVolumeReqDTO) {
		//볼륨 삭제
		storageModuleService.deleteVolumeByMetaName(deleteVolumeReqDTO.toModuleDto());
	}

	@Override
	public PageResDTO findVolumesWithPagination(String workspaceMetaName, Pageable pageable,
		SearchCondition searchCondition) {
		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();
		String option = searchCondition.getOption();
		String keyword = searchCondition.getKeyword();

		FindVolumeDTO findVolumeDTO = FindVolumeDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.option(option)
			.keyword(keyword)
			.build();

		return storageModuleService.findVolumesWithPagination(findVolumeDTO);
	}
}
