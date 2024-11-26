package com.xiilab.servercore.workspace.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.servercore.workspace.dto.WorkspaceResourceSettingDTO;
import com.xiilab.servercore.workspace.entity.WorkspaceSettingEntity;
import com.xiilab.servercore.workspace.repository.WorkspaceSettingRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class WorkspaceSettingServiceImpl implements WorkspaceSettingService {
	private final WorkspaceSettingRepo workspaceSettingRepo;

	@Override
	public WorkspaceResourceSettingDTO getWorkspaceResourceSetting() {
		WorkspaceSettingEntity workspaceSettingEntity = workspaceSettingRepo.findAll().get(0);
		return new WorkspaceResourceSettingDTO(workspaceSettingEntity.getCpu(), workspaceSettingEntity.getMem(),
			workspaceSettingEntity.getGpu(), workspaceSettingEntity.getWorkspaceCreateLimit(),
			workspaceSettingEntity.getWorkloadPendingCreateYN());
	}
}
