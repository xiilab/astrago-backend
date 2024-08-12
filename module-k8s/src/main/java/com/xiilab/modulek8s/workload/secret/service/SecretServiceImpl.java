package com.xiilab.modulek8s.workload.secret.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.deploy.dto.request.ModuleCreateDeployReqDTO;
import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
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
	public String createSecret(K8SResourceReqDTO moduleCreateWorkloadReqDTO) {
		if(moduleCreateWorkloadReqDTO instanceof ModuleCreateWorkloadReqDTO){
			ModuleCreateWorkloadReqDTO createWorkloadReqDTO = (ModuleCreateWorkloadReqDTO)moduleCreateWorkloadReqDTO;
			return secretRepository.createSecret(createWorkloadReqDTO.toCredentialVO());
		}else if(moduleCreateWorkloadReqDTO instanceof ModuleCreateDeployReqDTO){
			ModuleCreateDeployReqDTO createWorkloadReqDTO = (ModuleCreateDeployReqDTO)moduleCreateWorkloadReqDTO;
			return secretRepository.createSecret(createWorkloadReqDTO.toCredentialVO());
		}
		return null;
	}

	@Override
	public String createIbmSecret(SecretDTO secretDTO) {
		return secretRepository.createIbmSecret(secretDTO);
	}

	@Override
	public void deleteIbmSecret(String secretName){
		secretRepository.deleteIbmSecret(secretName);
	}
}
