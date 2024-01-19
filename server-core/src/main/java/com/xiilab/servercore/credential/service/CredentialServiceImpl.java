package com.xiilab.servercore.credential.service;

import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.credential.dto.CredentialReqDTO;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.entity.CredentialEntity;
import com.xiilab.servercore.credential.repository.CredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
	private final CredentialRepository credentialRepository;

	@Override
	public CredentialResDTO createCredential(CredentialReqDTO credentialReqDTO, UserInfoDTO userInfoDTO) {
		CredentialEntity credentialEntity = credentialRepository.save(CredentialEntity
			.dtoConverter()
			.credentialReqDTO(credentialReqDTO)
			.build());
		return new CredentialResDTO.CredentialInfo(credentialEntity);
	}

	@Override
	public CredentialResDTO.CredentialInfo findCredentialById(long id, UserInfoDTO userInfoDTO) {
		CredentialEntity credentialEntity = credentialRepository.findById(id)
			.orElseThrow(IllegalArgumentException::new);
		return new CredentialResDTO.CredentialInfo(credentialEntity);
	}

	@Override
	public Page<CredentialResDTO> getCredentialList(Pageable pageable, UserInfoDTO userInfoDTO) {
		Page<CredentialEntity> credentialEntities = null;
		if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN) {
			credentialEntities = credentialRepository.findAll(pageable);
		} else {
			credentialEntities = credentialRepository.findByUser_Name(userInfoDTO.getUserName(), pageable);
		}
		return Objects.requireNonNull(credentialEntities).map(CredentialResDTO::new);
	}

	@Override
	public void deleteCredentialById(long id, UserInfoDTO userInfoDTO) {
		credentialRepository.deleteById(id);
	}
}
