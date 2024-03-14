package com.xiilab.servercore.alert.systemalert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAlertServiceImpl implements SystemAlertService {

	private final SystemAlertRepository systemAlertRepository;
	@Override
	public void sendAlert(SystemAlertDTO systemAlertDTO){
		SystemAlertEntity systemAlertEntity = systemAlertRepository.save(SystemAlertDTO.convertEntity(systemAlertDTO));
		SystemAlertDTO.ResponseDTO.convertResponseDTO(systemAlertEntity);
	}
	@Override
	public SystemAlertDTO.ResponseDTO getAlertById(long id) {
		SystemAlertEntity systemAlertEntity = getAlertEntity(id);
		if(!systemAlertEntity.isReadYN()){
			systemAlertEntity.readAlert();
		}
		return SystemAlertDTO.ResponseDTO.convertResponseDTO(systemAlertEntity);
	}

	@Override
	public List<SystemAlertDTO.ResponseDTO> getAlertListByUserId(String recipientId) {
		List<SystemAlertEntity> systemAlertEntityList = systemAlertRepository.getAlertEntitiesByRecipientId(recipientId);
		return systemAlertEntityList.stream().map(SystemAlertDTO.ResponseDTO::convertResponseDTO).toList();
	}

	@Override
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

	private SystemAlertEntity getAlertEntity(long id){
		return systemAlertRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorCode.ALERT_NOT_FOUND));
	}
}
