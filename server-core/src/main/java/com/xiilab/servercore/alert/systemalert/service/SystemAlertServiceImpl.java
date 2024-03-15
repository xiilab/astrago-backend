package com.xiilab.servercore.alert.systemalert.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.SystemAlertErrorCode;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.servercore.alert.systemalert.dto.request.SystemAlertReqDTO;
import com.xiilab.servercore.alert.systemalert.dto.response.FindSystemAlertResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAlertServiceImpl implements SystemAlertService {

	private final SystemAlertRepository systemAlertRepository;

	@Override
	public Long saveSystemAlert(SystemAlertReqDTO.SaveSystemAlert saveSystemAlertReqDTO) {
		SystemAlertEntity saveSystemAlert = SystemAlertEntity.builder()
			.title(saveSystemAlertReqDTO.getTitle())
			.message(saveSystemAlertReqDTO.getMessage())
			.recipientId(saveSystemAlertReqDTO.getRecipientId())
			.senderId(saveSystemAlertReqDTO.getSenderId())
			.systemAlertType(saveSystemAlertReqDTO.getSystemAlertType())
			.systemAlertEventType(saveSystemAlertReqDTO.getSystemAlertEventType())
			.build();
		return systemAlertRepository.save(saveSystemAlert).getId();
	}

	@Override
	public FindSystemAlertResDTO.SystemAlertDetail getSystemAlertById(Long id) {
		SystemAlertEntity systemAlertEntity = systemAlertRepository.findById(id)
			.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_SYSTEM_ALERT));
		return FindSystemAlertResDTO.SystemAlertDetail.from(systemAlertEntity);
	}

	@Override
	public FindSystemAlertResDTO.SystemAlerts getSystemAlerts(String recipientId, Pageable pageable) {
		Page<SystemAlertEntity> systemAlertEntities = systemAlertRepository.findAlerts(recipientId, pageable);
		return FindSystemAlertResDTO.SystemAlerts.from(systemAlertEntities.getContent(), systemAlertEntities.getTotalElements());
	}

	@Override
	public void readSystemAlert(Long id) {
		SystemAlertEntity systemAlertEntity = systemAlertRepository.findById(id)
			.orElseThrow(() -> new RestApiException(SystemAlertErrorCode.NOT_FOUND_SYSTEM_ALERT));
		systemAlertEntity.readAlert();
		systemAlertRepository.save(systemAlertEntity);
	}

	@Override
	public void deleteSystemAlertById(Long id) {
		systemAlertRepository.deleteById(id);
	}
}
