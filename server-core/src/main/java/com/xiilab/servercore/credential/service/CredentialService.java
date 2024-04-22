package com.xiilab.servercore.credential.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.credential.dto.CredentialReqDTO;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.credential.dto.CredentialResDTO;

public interface CredentialService {
	CredentialResDTO createCredential(CredentialReqDTO credentialReqDTO, UserDTO.UserInfo userInfoDTO);
	CredentialResDTO.CredentialInfo findCredentialById(long id, UserDTO.UserInfo userInfoDTO);
	Page<CredentialResDTO> getCredentialList(Pageable pageable, UserDTO.UserInfo userInfoDTO);
	void deleteCredentialById(long id, UserDTO.UserInfo userInfoDTO);
	void updateCredentialById(long id, CredentialReqDTO.UpdateDTO updateDTO, UserDTO.UserInfo userInfoDTO);
	CredentialEntity getCredentialEntity(long id);
	CredentialResDTO.CredentialInfos findCredentialByIdIn(List<Long> credentialIds, Pageable pageable);
}
