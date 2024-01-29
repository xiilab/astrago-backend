package com.xiilab.servercore.workspace.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
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
	private final GroupService groupService;
	private final PinService pinService;

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
	public List<WorkspaceDTO.ResponseDTO> getWorkspaceList(UserInfoDTO userInfoDTO) {
		Set<String> groupList = userInfoDTO.getWorkspaceList();
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		return workspaceList.stream().filter(workspace -> groupList.contains(workspace.getResourceName())).toList();
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
}
