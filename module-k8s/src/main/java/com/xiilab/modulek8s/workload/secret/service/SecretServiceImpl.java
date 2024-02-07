package com.xiilab.modulek8s.workload.secret.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.secret.repository.SecretRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecretServiceImpl implements SecretService {
	private final SecretRepository secretRepository;

	@Override
	public String createSecret(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return secretRepository.createSecret(moduleCreateWorkloadReqDTO.toCredentialVO());
	}
}
