package com.xiilab.servercore.pin.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		List<PinEntity> workspacePins = pinRepository.findByTypeAndAndRegUser_RegUserId(PinType.WORKSPACE, userId);
		return workspacePins.stream().map(PinEntity::getResourceId).collect(Collectors.toSet());
	}

	@Override
	public Set<String> getUserWorkloadPinList(String userId) {
		List<PinEntity> workloadPins = pinRepository.findByTypeAndAndRegUser_RegUserId(PinType.WORKLOAD, userId);
		return workloadPins.stream().map(PinEntity::getResourceId).collect(Collectors.toSet());
	}

	@Override
	public void createWorkspacePin(String resourceId, UserInfoDTO userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceIdAndRegUser_RegUserId(PinType.WORKSPACE,
			resourceId, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new IllegalArgumentException("이미 pin이 추가되었습니다.");
		}

		//해당 유저가 pin을 6개 이상 생성했는지 검사
		List<PinEntity> workspaceList = pinRepository.findByTypeAndAndRegUser_RegUserId(PinType.WORKSPACE, userInfoDTO.getId());
		if (workspaceList.size() >= 6) {
			throw new IllegalArgumentException("");
		}
		pinRepository.save(new PinEntity(PinType.WORKSPACE, resourceId));
	}

	@Override
	public void createWorkloadPin(String resourceId, UserInfoDTO userInfoDTO) {
		PinEntity pinEntity = pinRepository.findByTypeAndResourceIdAndRegUser_RegUserId(PinType.WORKLOAD,
			resourceId, userInfoDTO.getId());

		if (pinEntity != null) {
			throw new IllegalArgumentException("이미 pin이 추가되었습니다.");
		}

		pinRepository.save(new PinEntity(PinType.WORKSPACE, resourceId));
	}

	@Override
	@Transactional
	public void deleteWorkspacePin(String resourceId, UserInfoDTO userInfoDTO) {
		pinRepository.deleteByTypeAndResourceIdAndRegUser_RegUserId(PinType.WORKSPACE, resourceId, userInfoDTO.getId());
	}

	@Override
	@Transactional
	public void deleteWorkloadPin(String resourceId, UserInfoDTO userInfoDTO) {
		pinRepository.deleteByTypeAndResourceIdAndRegUser_RegUserId(PinType.WORKLOAD, resourceId, userInfoDTO.getId());
	}
}
