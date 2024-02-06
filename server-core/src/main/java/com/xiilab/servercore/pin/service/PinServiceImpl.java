package com.xiilab.servercore.pin.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.entity.PinEntity;
import com.xiilab.servercore.pin.enumeration.PinType;
import com.xiilab.servercore.pin.repository.PinRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {
	private final PinRepository pinRepository;

	@Override
	public Set<String> getUserWorkspacePinList(String userId) {
		List<PinEntity> workspacePins = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKSPACE, userId);
		return workspacePins.stream().map(PinEntity::getResourceName).collect(Collectors.toSet());
	}

	@Override
	public Set<String> getUserWorkloadPinList(String userId, String workspaceName) {
		List<PinEntity> workloadPins = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKLOAD, userId);
		return workloadPins.stream().map(PinEntity::getResourceName).collect(Collectors.toSet());
	}

	@Override
	public void createPin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO) {
		if (pinType == PinType.WORKLOAD) {
			createWorkloadPin(resourceName, userInfoDTO);
		} else {
			createWorkspacePin(resourceName, userInfoDTO);
		}
	}

	@Override
	@Transactional
	public void deletePin(String resourceName, PinType pinType, UserInfoDTO userInfoDTO) {
		if (pinType == PinType.WORKLOAD) {
			deleteWorkloadPin(resourceName, userInfoDTO);
		} else {
			deleteWorkspacePin(resourceName, userInfoDTO);
		}
	}

	private void createWorkspacePin(String resourceName, UserInfoDTO userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKSPACE,
			resourceName, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_PIN_ERROR);
		}

		//해당 유저가 pin을 6개 이상 생성했는지 검사
		List<PinEntity> workspaceList = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKSPACE, userInfoDTO.getId());
		if (workspaceList.size() >= 6) {
			throw new IllegalArgumentException("");
		}
		pinRepository.save(new PinEntity(PinType.WORKSPACE, resourceName));
	}

	private void createWorkloadPin(String resourceName, UserInfoDTO userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKLOAD,
			resourceName, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_PIN_ERROR);
		}

		pinRepository.save(new PinEntity(PinType.WORKSPACE, resourceName));
	}

	private void deleteWorkspacePin(String resourceName, UserInfoDTO userInfoDTO) {
		pinRepository.deleteByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKSPACE, resourceName, userInfoDTO.getId());
	}

	private void deleteWorkloadPin(String resourceId, UserInfoDTO userInfoDTO) {
		pinRepository.deleteByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKLOAD, resourceId, userInfoDTO.getId());
	}
}
