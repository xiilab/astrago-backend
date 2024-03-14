package com.xiilab.servercore.alert.systemalert.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertSetEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertSetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceAlertSetServiceImpl implements WorkspaceAlertSetService {
	private final WorkspaceAlertSetRepository workspaceAlertSetRepository;

	@Override
	@Transactional
	public void saveAlertSet(String workspaceName) {
		try{
			workspaceAlertSetRepository.save(WorkspaceAlertSetEntity.builder()
				.workspaceName(workspaceName)
				.workloadStartAlert(true)
				.workloadEndAlert(true)
				.workloadErrorAlert(true)
				.resourceApprovalAlert(true)
				.build());
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_SAVE_FAIL);
		}
	}

	@Override
	public WorkspaceAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName) {
		if(workspaceName.contains("ws")){
			WorkspaceAlertSetEntity workspaceAlertSetEntity = getAlertSetEntity(workspaceName);
			return WorkspaceAlertSetDTO.ResponseDTO.convertResponseDTO(workspaceAlertSetEntity);
		}else {
			return WorkspaceAlertSetDTO.ResponseDTO.builder()
				.workloadEndAlert(false)
				.workloadErrorAlert(false)
				.workloadStartAlert(false)
				.resourceApprovalAlert(false)
				.resourceApprovalAlert(false)
				.build();
		}
	}

	@Override
	@Transactional
	public WorkspaceAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, WorkspaceAlertSetDTO workspaceAlertSetDTO){
		try{
			WorkspaceAlertSetEntity workspaceAlertSetEntity = getAlertSetEntity(workspaceName);

			WorkspaceAlertSetEntity updateAlertSet = workspaceAlertSetEntity.updateWorkspaceAlertSet(workspaceAlertSetDTO);

			return WorkspaceAlertSetDTO.ResponseDTO.convertResponseDTO(updateAlertSet);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_UPDATE_FAIL);
		}
	}
	@Override
	@Transactional
	public void deleteAlert(String workspaceName){
		try{
			WorkspaceAlertSetEntity workspaceAlertSetEntity = getAlertSetEntity(workspaceName);

			workspaceAlertSetRepository.deleteById(workspaceAlertSetEntity.getId());

		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_DELETE_FAIL);
		}
	}

	private WorkspaceAlertSetEntity getAlertSetEntity(String workspaceName){
		try{
			return workspaceAlertSetRepository.getAlertSetEntityByWorkspaceName(workspaceName);
		}catch (RuntimeException e){
			throw new RestApiException(CommonErrorCode.ALERT_NOT_FOUND_WORKSPACE_NAME);
		}
	}
}
