package com.xiilab.servercore.credential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.credential.dto.CredentialReqDTO;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.entity.CredentialEntity;

@Service
public interface CredentialService {
	CredentialResDTO createCredential(CredentialReqDTO credentialReqDTO, UserInfoDTO userInfoDTO);
	CredentialResDTO.CredentialInfo findCredentialById(long id, UserInfoDTO userInfoDTO);
	Page<CredentialResDTO> getCredentialList(Pageable pageable, UserInfoDTO userInfoDTO);
	void deleteCredentialById(long id, UserInfoDTO userInfoDTO);
	void updateCredentialById(long id, CredentialReqDTO.UpdateDTO updateDTO, UserInfoDTO userInfoDTO);
	CredentialEntity getCredentialEntity(long id);
}
