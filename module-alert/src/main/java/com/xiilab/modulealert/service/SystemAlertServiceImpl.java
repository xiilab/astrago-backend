package com.xiilab.modulealert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.SystemAlertDTO;
import com.xiilab.modulealert.entity.SystemAlertEntity;
import com.xiilab.modulealert.repository.SystemAlertRepository;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAlertServiceImpl implements SystemAlertService {
	private final SystemAlertRepository systemAlertRepository;

	@Override
	public void sendAlert(SystemAlertDTO systemAlertDTO){
		SystemAlertEntity systemAlertEntity = systemAlertRepository.save(SystemAlertDTO.convertEntity(systemAlertDTO));
		SystemAlertDTO.ResponseDTOSystem.convertResponseDTO(systemAlertEntity);
	}

	@Override
	public List<SystemAlertDTO.ResponseDTOSystem> getAlertListByUserId(String recipientId) {
		List<SystemAlertEntity> systemAlertEntityList = systemAlertRepository.getAlertEntitiesByRecipientId(recipientId);
		return systemAlertEntityList.stream().map(SystemAlertDTO.ResponseDTOSystem::convertResponseDTO).toList();
	}

	@Override
	@Transactional
	public void readAlert(long id) {
		SystemAlertEntity systemAlertEntity = getAlertEntity(id);
		systemAlertEntity.readAlert();
	}

	@Override
	@Transactional
	public void deleteAlertById(long id){
		// 해당 ID의 Alert 존재 확인
		SystemAlertEntity systemAlertEntity = getAlertEntity(id);
		// 해당 ID의 Alert 삭제
		systemAlertRepository.deleteById(systemAlertEntity.getId());
	}
	@Override
	public SystemAlertDTO.ResponseDTOSystem getAlertById(long id){
		SystemAlertEntity systemAlertEntity = getAlertEntity(id);
		if(!systemAlertEntity.isReadYN()){
			systemAlertEntity.readAlert();
		}
		return SystemAlertDTO.ResponseDTOSystem.convertResponseDTO(systemAlertEntity);
	}

	private SystemAlertEntity getAlertEntity(long id){
		return systemAlertRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorCode.ALERT_NOT_FOUND));
	}
}
