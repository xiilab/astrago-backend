package com.xiilab.modulealert.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.AlertSetDTO;
import com.xiilab.modulealert.entity.AlertSetEntity;
import com.xiilab.modulealert.repository.AlertSetRepository;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertSetServiceImpl implements AlertSetService {

	private final AlertSetRepository alertSetRepository;

	@Override
	@Transactional
	public void saveAlertSet(String workspaceName) {
		try{
			alertSetRepository.save(AlertSetEntity.builder()
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
	public AlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName) {
		if(workspaceName.contains("ws")){
			AlertSetEntity alertSetEntity = getAlertSetEntity(workspaceName);
			return AlertSetDTO.ResponseDTO.convertResponseDTO(alertSetEntity);
		}else {
			return AlertSetDTO.ResponseDTO.builder()
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
	public AlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, AlertSetDTO alertSetDTO){
		try{
			AlertSetEntity alertSetEntity = getAlertSetEntity(workspaceName);

			AlertSetEntity updateAlertSet = alertSetEntity.updateAlertSet(alertSetDTO);

			return AlertSetDTO.ResponseDTO.convertResponseDTO(updateAlertSet);
		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_UPDATE_FAIL);
		}
	}
	@Override
	@Transactional
	public void deleteAlert(String workspaceName){
		try{
			AlertSetEntity alertSetEntity = getAlertSetEntity(workspaceName);

			alertSetRepository.deleteById(alertSetEntity.getId());

		}catch (IllegalArgumentException e){
			throw new RestApiException(CommonErrorCode.ALERT_SET_DELETE_FAIL);
		}
	}

	private AlertSetEntity getAlertSetEntity(String workspaceName){
		try{
			return alertSetRepository.getAlertSetEntityByWorkspaceName(workspaceName);
		}catch (RuntimeException e){
			throw new RestApiException(CommonErrorCode.ALERT_NOT_FOUND_WORKSPACE_NAME);
		}
	}

}
