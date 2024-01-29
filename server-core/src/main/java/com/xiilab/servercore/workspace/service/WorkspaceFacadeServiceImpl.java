package com.xiilab.servercore.workspace.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeService;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceFacadeServiceImpl implements WorkspaceFacadeService {
	private final WorkspaceModuleFacadeService workspaceModuleFacadeService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
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
	public List<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition,
		UserInfoDTO userInfoDTO) {
		Set<String> groupList = userInfoDTO.getWorkspaceList(isMyWorkspace);
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());
		//조건절 처리
		workspaceList = workspaceList.stream()
			.filter(workspace -> groupList.contains(workspace.getResourceName()))
			.filter(workspace -> searchCondition == null || workspace.getName().contains(searchCondition))
			.toList();
		//pin YN 처리
		return workspaceList.stream()
			.map(workspace -> new WorkspaceDTO.TotalResponseDTO(
				workspace.getId(),
				workspace.getName(),
				workspace.getResourceName(),
				workspace.getDescription(),
				userWorkspacePinList.contains(workspace.getId()),
				workspace.getCreatedAt(),
				workloadModuleFacadeService.getUserRecentlyWorkload(workspace.getResourceName(), userInfoDTO.getUserName())))
			.toList();
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName) {
		workspaceModuleFacadeService.deleteWorkspaceByName(workspaceName);
		groupService.deleteWorkspaceGroupByName(workspaceName);
	}
}
