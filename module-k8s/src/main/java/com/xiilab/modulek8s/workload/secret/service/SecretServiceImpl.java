package com.xiilab.modulek8s.workload.secret.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.secret.repository.SecretRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecretServiceImpl implements SecretService {
	private final SecretRepository secretRepository;

	@Override
	public String createSecret(CreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return secretRepository.createSecret(moduleCreateWorkloadReqDTO.toCredentialVO());
	}

	@Override
	public String createIbmSecret(SecretDTO secretDTO) {
		return secretRepository.createIbmSecret(secretDTO);
	}
}
