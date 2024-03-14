package com.xiilab.servercore.alert.systemalert.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertSetRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemAlertSetServiceImpl implements SystemAlertSetService{
	private final SystemAlertSetRepository systemAlertSetRepository;
	@Override
	public SystemAlertSetDTO.ResponseDTO getSystemAlertSet() {

		SystemAlertSetEntity systemAlertSetEntity = getSystemAlertSetEntity();

		return SystemAlertSetDTO.ResponseDTO.receiveDTOBuilder()
			.entity(systemAlertSetEntity)
			.build();
	}

	@Override
	@Transactional
	public void updateSystemAlertSet(SystemAlertSetDTO systemAlertSetDTO) {
		SystemAlertSetEntity systemAlertSetEntity = getSystemAlertSetEntity();
		systemAlertSetEntity.updateSystemAlertSet(systemAlertSetDTO);

	}

	private SystemAlertSetEntity getSystemAlertSetEntity() {
		SystemAlertSetEntity systemAlertSetEntity = systemAlertSetRepository.findById(1L).get();
		return systemAlertSetEntity;
	}

}
