package com.xiilab.modulek8s.workspace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.repository.WorkspaceRepo;
import com.xiilab.modulek8s.workspace.vo.WorkspaceResVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
	private final WorkspaceRepo workspaceRepo;

	@Override
	public WorkspaceDTO.ResponseDTO createWorkspace(WorkspaceDTO.RequestDTO workspaceReqDTO) {
		WorkspaceResVO workspace = workspaceRepo.createWorkSpace(workspaceReqDTO.convertToVO());
		return new WorkspaceDTO.ResponseDTO(
			workspace.getUid(),
			workspace.getName(),
			workspace.getResourceName(),
			workspace.getDescription(),
			workspace.getCreatorId(),
			workspace.getCreatorUserName(),
			workspace.getCreatorFullName(),
			workspace.getCreatedAt());
	}

	@Override
	public WorkspaceDTO.ResponseDTO getWorkspaceByName(String workspaceName) {
		WorkspaceResVO workspace = workspaceRepo.getWorkspaceByName(workspaceName);
		return new WorkspaceDTO.ResponseDTO(
			workspace.getUid(),
			workspace.getName(),
			workspace.getResourceName(),
			workspace.getDescription(),
			workspace.getCreatorId(),
			workspace.getCreatorUserName(),
			workspace.getCreatorFullName(),
			workspace.getCreatedAt());
	}

	@Override
	public List<WorkspaceDTO.ResponseDTO> getWorkspaceList() {
		List<WorkspaceResVO> workspaceList = workspaceRepo.getWorkspaceList();
		return workspaceList.stream().map(workspace
			-> new WorkspaceDTO.ResponseDTO(
			workspace.getUid(),
			workspace.getName(),
			workspace.getResourceName(),
			workspace.getDescription(),
			workspace.getCreatorId(),
			workspace.getCreatorUserName(),
			workspace.getCreatorFullName(),
			workspace.getCreatedAt()
		)).toList();
	}

	@Override
	public void updateWorkspace(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO) {
		workspaceRepo.updateWorkspaceInfo(workspaceName, updateDTO);
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName) {
		workspaceRepo.deleteWorkspaceByName(workspaceName);
	}
}
