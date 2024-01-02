package com.xiilab.servercore.workspace.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.PageFindVolumeDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaReqDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.service.ResourceQuotaService;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.DeleteWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceTotalDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceFacadeServiceImpl implements WorkspaceFacadeService {
	private final StorageModuleService storageModuleService;
	private final WorkspaceService workspaceService;
	private final ResourceQuotaService resourceQuotaService;
	private final GroupService groupService;

	@Override
	public void createWorkspace(WorkspaceApplicationForm applicationForm) {
		//워크스페이스 생성
		WorkspaceDTO.ResponseDTO workspace = workspaceService.createWorkspace(new WorkspaceDTO.RequestDTO(
			applicationForm.getName(),
			applicationForm.getDescription(),
			LocalDateTime.now(),
			applicationForm.getCreatorName(),
			applicationForm.getCreator()
		));
		//워크스페이스 resource quota 생성
		resourceQuotaService.createResourceQuotas(
			ResourceQuotaReqDTO.builder()
				.name(applicationForm.getName())
				.namespace(workspace.getResourceName())
				.description(applicationForm.getDescription())
				.creator(applicationForm.getCreator())
				.creatorName(applicationForm.getCreator())
				.reqCPU(applicationForm.getReqCPU())
				.reqGPU(applicationForm.getReqGPU())
				.reqMEM(applicationForm.getReqMEM())
				.reqDisk(applicationForm.getReqDisk())
				.build());
		//그룹 생성
		groupService.createWorkspaceGroup(
			GroupReqDTO.builder()
				.name(workspace.getResourceName())
				.createdBy(applicationForm.getCreatorName())
				.createdUserId(applicationForm.getCreator())
				.description(applicationForm.getDescription())
				.users(applicationForm.getUserIds())
				.build());
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName) {
		workspaceService.deleteWorkspaceByName(workspaceName);
		groupService.deleteWorkspaceGroupByName(workspaceName);
	}

	@Override
	public List<WorkspaceDTO.ResponseDTO> getWorkspaceList(UserInfoDTO userInfoDTO) {
		Set<String> groupList = userInfoDTO.getWorkspaceList();
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceService.getWorkspaceList();
		return workspaceList.stream().filter(workspace -> groupList.contains(workspace.getResourceName())).toList();
	}

	@Override
	public WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceName) {
		WorkspaceDTO.ResponseDTO workspaceByName = workspaceService.getWorkspaceByName(workspaceName);
		ResourceQuotaResDTO resourceQuotas = resourceQuotaService.getResourceQuotas(workspaceName, workspaceName);
		return new WorkspaceTotalDTO(workspaceByName, resourceQuotas);
	}

	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
		return storageModuleService.findVolumesByWorkspaceMetaNameAndStorageMetaName(workspaceMetaName,
			storageMetaName);
	}

	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName) {
		return storageModuleService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	@Override
	public void modifyVolumeByMetaName(ModifyWorkspaceVolumeReqDTO modifyWorkspaceVolumeReqDTO) {
		storageModuleService.modifyVolumeByMetaName(modifyWorkspaceVolumeReqDTO.toModuleDto());
	}

	@Override
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(
		DeleteWorkspaceVolumeReqDTO deleteWorkspaceVolumeReqDTO) {
		//볼륨 삭제
		storageModuleService.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(
			deleteWorkspaceVolumeReqDTO.toModuleDto());
	}

	@Override
	public PageResDTO findVolumesWithPagination(String workspaceMetaName, Pageable pageable,
		SearchCondition searchCondition) {
		int pageNumber = pageable.getPageNumber();
		int pageSize = pageable.getPageSize();
		String option = searchCondition.getOption();
		String keyword = searchCondition.getKeyword();

		PageFindVolumeDTO pageFindVolumeDTO = PageFindVolumeDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.option(option)
			.keyword(keyword)
			.build();

		return storageModuleService.findVolumesWithPagination(pageFindVolumeDTO);
	}
}
