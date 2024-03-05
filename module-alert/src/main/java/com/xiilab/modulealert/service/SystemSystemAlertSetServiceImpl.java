package com.xiilab.modulealert.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.SystemAlertSetDTO;
import com.xiilab.modulealert.entity.SystemAlertSetEntity;
import com.xiilab.modulealert.repository.SystemAlertSetRepository;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemSystemAlertSetServiceImpl implements SystemAlertSetService {

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
	public SystemAlertSetDTO.ResponseDTOSystem getWorkspaceAlertSet(String workspaceName) {
		if(workspaceName.contains("ws")){
			SystemAlertSetEntity systemAlertSetEntity = getAlertSetEntity(workspaceName);
			return SystemAlertSetDTO.ResponseDTOSystem.convertResponseDTO(systemAlertSetEntity);
		}else {
			return SystemAlertSetDTO.ResponseDTOSystem.builder()
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
	public SystemAlertSetDTO.ResponseDTOSystem updateWorkspaceAlertSet(String workspaceName, SystemAlertSetDTO systemAlertSetDTO){
		try{
			SystemAlertSetEntity systemAlertSetEntity = getAlertSetEntity(workspaceName);

			SystemAlertSetEntity updateAlertSet = systemAlertSetEntity.updateAlertSet(systemAlertSetDTO);

			return SystemAlertSetDTO.ResponseDTOSystem.convertResponseDTO(updateAlertSet);
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
