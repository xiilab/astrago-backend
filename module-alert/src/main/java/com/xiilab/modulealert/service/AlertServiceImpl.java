package com.xiilab.modulealert.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulealert.dto.AlertDTO;
import com.xiilab.modulealert.entity.AlertEntity;
import com.xiilab.modulealert.repository.AlertRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
	private AlertRepository alertRepository;

	@Override
	public void sendAlert(AlertDTO alertDTO){
		AlertEntity alertEntity = alertRepository.save(AlertDTO.convertEntity(alertDTO));
		AlertDTO.ResponseDTO.convertResponseDTO(alertEntity);
	}

	@Override
	public List<AlertDTO.ResponseDTO> getAlertListByUserId(String recipientId) {
		List<AlertEntity> alertEntityList = alertRepository.getAlertEntitiesByRecipientId(recipientId);
		return alertEntityList.stream().map(AlertDTO.ResponseDTO::convertResponseDTO).toList();
	}

	@Override
	@Transactional
	public void readAlert(long id) {
		AlertEntity alertEntity = getAlertEntity(id);
		alertEntity.readAlert();
	}

	@Override
	@Transactional
	public void deleteAlertById(long id){
		// 해당 ID의 Alert 존재 확인
		AlertEntity alertEntity = getAlertEntity(id);
		// 해당 ID의 Alert 삭제
		alertRepository.deleteById(alertEntity.getId());
	}
	@Override
	public AlertDTO.ResponseDTO getAlertById(long id){
		AlertEntity alertEntity = getAlertEntity(id);
		if(!alertEntity.isReadYN()){
			alertEntity.readAlert();
		}
		return AlertDTO.ResponseDTO.convertResponseDTO(alertEntity);
	}

	private AlertEntity getAlertEntity(long id){
		return alertRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 ID(" + id + ") 의 Alert이 없습니다."));
	}
}
