package com.xiilab.servercore.workspace.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeService;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.servercore.workspace.entity.ResourceQuotaEntity;
import com.xiilab.servercore.workspace.repository.ResourceQuotaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceFacadeServiceImpl implements WorkspaceFacadeService {
	private final WorkspaceModuleFacadeService workspaceModuleFacadeService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ResourceQuotaRepository resourceQuotaRepository;
	private final PinService pinService;
	private final GroupService groupService;

	@Override
	public void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO) {
		//워크스페이스 생성
		WorkspaceDTO.ResponseDTO workspace = workspaceModuleFacadeService.createWorkspace(CreateWorkspaceDTO.builder()
			.name(applicationForm.getName())
			.description(applicationForm.getDescription())
			.creatorName(userInfoDTO.getUserName())
			.creator(userInfoDTO.getId())
			.reqCPU(applicationForm.getReqCPU())
			.reqMEM(applicationForm.getReqMEM())
			.reqGPU(applicationForm.getReqGPU())
			.reqDisk(applicationForm.getReqDisk())
			.build());
		//group 추가
		groupService.createWorkspaceGroup(
			GroupReqDTO.builder()
				.name(workspace.getResourceName())
				.createdBy(applicationForm.getCreatorName())
				.createdUserId(applicationForm.getCreator())
				.description(applicationForm.getDescription())
				.users(applicationForm.getUserIds())
				.build()
		);
	}

	@Override
	public PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition,
		int pageNum, UserInfoDTO userInfoDTO) {
		Set<String> groupList = userInfoDTO.getWorkspaceList(isMyWorkspace);
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());
		//조건절 처리
		workspaceList = workspaceList.stream()
			.filter(workspace -> groupList.contains(workspace.getResourceName()))
			.filter(workspace -> searchCondition == null || workspace.getName().contains(searchCondition))
			.sorted(Comparator.comparing(WorkspaceDTO.ResponseDTO::getCreatedAt).reversed())
			.toList();
		//페이지네이션 진행
		PageDTO<WorkspaceDTO.ResponseDTO> pageDTO = new PageDTO<>(workspaceList, pageNum, 9);
		//pinYN 처리 및 최근 워크로드 불러오기 진행
		//최적화를 위해 pageNation 후에 최근워크로드 조회 작업을 진행
		List<WorkspaceDTO.TotalResponseDTO> resultList = pageDTO.getContent()
			.stream()
			.map(workspace -> new WorkspaceDTO.TotalResponseDTO(
				workspace.getId(),
				workspace.getName(),
				workspace.getResourceName(),
				workspace.getDescription(),
				userWorkspacePinList.contains(workspace.getId()),
				workspace.getCreatedAt(),
				workloadModuleFacadeService.getUserRecentlyWorkload(workspace.getResourceName(),
					userInfoDTO.getUserName())))
			.toList();
		return new PageDTO<>(resultList, pageNum, 9);
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName) {
		workspaceModuleFacadeService.deleteWorkspaceByName(workspaceName);
		groupService.deleteWorkspaceGroupByName(workspaceName);
	}

	@Override
	public List<WorkspaceDTO.ResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO) {
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());
		return workspaceModuleFacadeService.getWorkspaceList().stream()
			.filter(workspace -> userWorkspacePinList.contains(workspace.getResourceName())).toList();
	}

	@Override
	public void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO, UserInfoDTO userInfoDTO) {
		resourceQuotaRepository.save(new ResourceQuotaEntity(workspaceResourceReqDTO));
	}

	@Override
	public List<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, UserInfoDTO userInfoDTO) {
		List<ResourceQuotaEntity> resourceQuotaReqList = resourceQuotaRepository.findByWorkspace(workspace);
		return resourceQuotaReqList.stream()
			.map(resourceQuotaEntity ->
				ResourceQuotaFormDTO.builder()
					.id(resourceQuotaEntity.getId())
					.workspace(resourceQuotaEntity.getWorkspace())
					.requestReason(resourceQuotaEntity.getRequestReason())
					.rejectReason(resourceQuotaEntity.getRejectReason())
					.status(resourceQuotaEntity.getStatus())
					.cpuReq(resourceQuotaEntity.getCpuReq())
					.gpuReq(resourceQuotaEntity.getGpuReq())
					.memReq(resourceQuotaEntity.getMemReq())
					.build())
			.toList();
	}

	@Override
	public void updateResourceQuota(String workspace, boolean approveYN) {

	}
}
