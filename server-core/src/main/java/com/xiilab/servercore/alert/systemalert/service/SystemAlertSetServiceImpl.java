package com.xiilab.servercore.alert.systemalert.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertSetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAlertSetServiceImpl implements SystemAlertSetService {
	private final SystemAlertSetRepository systemAlertSetRepository;

	@Override
	@Transactional
	public void saveAlertSet(String workspaceName) {
		try{
			systemAlertSetRepository.save(SystemAlertSetEntity.builder()
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
	public SystemAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName) {
		if(workspaceName.contains("ws")){
			SystemAlertSetEntity systemAlertSetEntity = getAlertSetEntity(workspaceName);
			return SystemAlertSetDTO.ResponseDTO.convertResponseDTO(systemAlertSetEntity);
		}else {
			return SystemAlertSetDTO.ResponseDTO.builder()
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
	public SystemAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, SystemAlertSetDTO systemAlertSetDTO){
		try{
			SystemAlertSetEntity systemAlertSetEntity = getAlertSetEntity(workspaceName);

			SystemAlertSetEntity updateAlertSet = systemAlertSetEntity.updateAlertSet(systemAlertSetDTO);

			return SystemAlertSetDTO.ResponseDTO.convertResponseDTO(updateAlertSet);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_UPDATE_FAIL);
		}
	}
	@Override
	@Transactional
	public void deleteAlert(String workspaceName){
		try{
			SystemAlertSetEntity systemAlertSetEntity = getAlertSetEntity(workspaceName);

			systemAlertSetRepository.deleteById(systemAlertSetEntity.getId());

		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_DELETE_FAIL);
		}
	}

	private SystemAlertSetEntity getAlertSetEntity(String workspaceName){
		try{
			return systemAlertSetRepository.getAlertSetEntityByWorkspaceName(workspaceName);
		}catch (RuntimeException e){
			throw new RestApiException(CommonErrorCode.ALERT_NOT_FOUND_WORKSPACE_NAME);
		}
	}
}
