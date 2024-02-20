package com.xiilab.servercore.credential.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.modulek8sdb.credential.dto.CredentialReqDTO;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
	private final CredentialRepository credentialRepository;

	@Override
	@Transactional
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
			.orElseThrow(() -> new RestApiException(CommonErrorCode.CREDENTIAL_NOT_FOUND));
		// if (userInfoDTO.getAuth() != AuthType.ROLE_ADMIN && !(credentialEntity.getRegUser()
		// 	.getRegUserId()
		// 	.equals(userInfoDTO.getId()))) {
		// 	//TODO 에러 처리에 대한 메시지 및 방안 고려해야함
		// 	throw new IllegalArgumentException();
		// }
		return new CredentialResDTO.CredentialInfo(credentialEntity);
	}

	@Override
	public Page<CredentialResDTO> getCredentialList(Pageable pageable, UserInfoDTO userInfoDTO) {
		Page<CredentialEntity> credentialEntities = null;
		if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN) {
			credentialEntities = credentialRepository.findAll(pageable);
		} else {
			credentialEntities = credentialRepository.findByRegUser_RegUserId(userInfoDTO.getId(), pageable);
		}
		return Objects.requireNonNull(credentialEntities).map(CredentialResDTO::new);
	}

	@Override
	public void deleteCredentialById(long id, UserInfoDTO userInfoDTO) {
		credentialRepository.deleteById(id);
	}

	@Override
	@Transactional
	public void updateCredentialById(long id, CredentialReqDTO.UpdateDTO updateDTO, UserInfoDTO userInfoDTO) {
		CredentialEntity credentialEntity = credentialRepository.findById(id).orElseThrow();

		if (!userInfoDTO.getId().equals(credentialEntity.getRegUser().getRegUserId())) {
			throw new IllegalArgumentException("해당 크리덴셜을 추가한 유저가 아닙니다.");
		}

		credentialEntity.updateInfo(updateDTO);
	}

	@Override
	public CredentialEntity getCredentialEntity(long id) {
		return credentialRepository.findById(id).orElseThrow(() -> new RestApiException(CommonErrorCode.CREDENTIAL_NOT_FOUND));
	}

	public CredentialResDTO.CredentialInfos findCredentialByIdIn(List<Long> credentialIds, Pageable pageable) {
		PageRequest pageRequest = null;
		if (pageable != null && !ObjectUtils.isEmpty(pageable.getPageNumber()) && !ObjectUtils.isEmpty(
			pageable.getPageSize())) {
			pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
		}
		Page<CredentialEntity> credential = credentialRepository.findByIdIn(credentialIds, pageRequest);
		List<CredentialEntity> result = credential.getContent();
		long totalElements = credential.getTotalElements();
		return CredentialResDTO.CredentialInfos.entitiesToDtos(result, totalElements);
	}
}
