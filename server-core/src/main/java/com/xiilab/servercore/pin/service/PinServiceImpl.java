package com.xiilab.servercore.pin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.PinErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.modulek8sdb.pin.entity.PinEntity;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.modulek8sdb.pin.repository.PinRepository;
import com.xiilab.moduleuser.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {
	private final PinRepository pinRepository;

	@Override
	public Set<String> getUserWorkspacePinList(String userId) {
		List<PinEntity> workspacePins = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKSPACE, userId);
		return workspacePins.stream().map(PinEntity::getResourceName).collect(Collectors.toSet());
	}

	@Override
	public Set<String> getUserWorkloadPinList(String userId) {
		List<PinEntity> workloadPins = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKLOAD, userId);
		return workloadPins.stream().map(PinEntity::getResourceName).collect(Collectors.toSet());
	}

	@Override
	public void createPin(String resourceName, PinType pinType, UserDTO.UserInfo userInfoDTO) {
		if (pinType == PinType.WORKLOAD) {
			createWorkloadPin(resourceName, userInfoDTO);
		} else {
			createWorkspacePin(resourceName, userInfoDTO);
		}
	}

	@Override
	@Transactional
	public void deletePin(String resourceName, PinType pinType, UserDTO.UserInfo userInfoDTO) {
		if (pinType == PinType.WORKLOAD) {
			deleteWorkloadPin(resourceName, userInfoDTO);
		} else {
			deleteWorkspacePin(resourceName, userInfoDTO);
		}
	}

	@Override
	public void deletePin(String resourceName, PinType pinType) {
		pinRepository.deleteByResourceNameAndType(resourceName, pinType);
	}

	@Override
	public List<String> getWorkloadPinListByUserId(String userId, String workspaceName) {
		List<PinEntity> pinList;
		if(workspaceName != null){
			pinList = pinRepository.findByTypeAndRegUserIdAndWorkspaceResourceName(userId, workspaceName, PinType.WORKLOAD.name());
		}else{
			pinList = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKLOAD,userId);
		}
		return pinList.stream().map(pinEntity -> pinEntity.getResourceName()).toList();
	}

	private void createWorkspacePin(String resourceName, UserDTO.UserInfo userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKSPACE,
			resourceName, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_PIN_ERROR);
		}

		//해당 유저가 pin을 6개 이상 생성했는지 검사
		List<PinEntity> workspaceList = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKSPACE,
			userInfoDTO.getId());
		if (workspaceList.size() >= 6) {
			throw new RestApiException(PinErrorCode.PIN_ADD_ERROR_MESSAGE);
		}
		pinRepository.save(new PinEntity(PinType.WORKSPACE, resourceName));
	}

	private void createWorkloadPin(String resourceName, UserDTO.UserInfo userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKLOAD,
			resourceName, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new RestApiException(WorkloadErrorCode.WORKLOAD_MESSAGE_ERROR);
		}

		//해당 유저가 pin을 6개 이상 생성했는지 검사
		List<PinEntity> pinList = pinRepository.findByTypeAndRegUser_RegUserId(PinType.WORKLOAD,
			userInfoDTO.getId());
		if (pinList.size() >= 6) {
			throw new RestApiException(PinErrorCode.PIN_ADD_ERROR_MESSAGE);
		}

		pinRepository.save(new PinEntity(PinType.WORKLOAD, resourceName));
	}

	private void deleteWorkspacePin(String resourceName, UserDTO.UserInfo userInfoDTO) {
		pinRepository.deleteByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKSPACE, resourceName,
			userInfoDTO.getId());
	}

	private void deleteWorkloadPin(String resourceId, UserDTO.UserInfo userInfoDTO) {
		pinRepository.deleteByTypeAndResourceNameAndRegUser_RegUserId(PinType.WORKLOAD, resourceId,
			userInfoDTO.getId());
	}
}
