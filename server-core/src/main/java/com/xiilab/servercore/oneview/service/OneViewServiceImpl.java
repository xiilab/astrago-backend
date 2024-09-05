package com.xiilab.servercore.oneview.service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.net.ssl.SSLException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientException;

import com.xiilab.modulecommon.enums.OneViewAccountConnectionStatus;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.OneViewErrorCode;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8sdb.oneview.entity.OneViewSettingEntity;
import com.xiilab.modulek8sdb.oneview.repository.OneViewSettingRepository;
import com.xiilab.servercore.dataset.service.WebClientService;
import com.xiilab.servercore.oneview.dto.OneViewReqDTO;
import com.xiilab.servercore.oneview.dto.OneViewResDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OneViewServiceImpl implements OneViewService {
	private final WebClientService webClientService;
	private final OneViewSettingRepository oneViewSettingRepository;

	@Override
	@Transactional
	public void saveOneViewSetting(OneViewReqDTO.SaveOneViewSetting saveOneViewSettingDTO) {
		try {
			// One View 버전 확인 API 호출
			OneViewResDTO.FindApiVersion findApiVersionResDTO = webClientService.getObjectFromUrl(
				saveOneViewSettingDTO.getApiServerAddress() + "/rest/version", OneViewResDTO.FindApiVersion.class);

			if (!Objects.isNull(findApiVersionResDTO.getCurrentVersion())) {
				OneViewResDTO.SessionToken sessionTokenResDTO = issueSessionTokenAPI(
					saveOneViewSettingDTO.getApiServerAddress(),
					findApiVersionResDTO.getCurrentVersion(),
					saveOneViewSettingDTO.getUserName(),
					saveOneViewSettingDTO.getPassword());

				if (!ValidUtils.isNullOrEmpty(sessionTokenResDTO.getSessionID())) {
					// 연동시 기존에 등록했던 데이터 삭제하고 등록
					oneViewSettingRepository.deleteAll();

					OneViewSettingEntity saveOneViewSetting = OneViewSettingEntity.builder()
						.apiServerAddress(saveOneViewSettingDTO.getApiServerAddress())
						.userName(saveOneViewSettingDTO.getUserName())
						.password(saveOneViewSettingDTO.getPassword())
						.apiVersion(findApiVersionResDTO.getCurrentVersion())
						.connectionFailedCount(0)
						.build();

					oneViewSettingRepository.save(saveOneViewSetting);
				}
			}
		} catch (SSLException e) {
			throw new RestApiException(OneViewErrorCode.FAILED_SSL_VERIFICATION_MESSAGE);
		}
	}

	@Override
	public OneViewAccountConnectionStatus getOneViewAccountConnectionStatus() {
		Optional<OneViewSettingEntity> optionalOneViewSettingEntity = oneViewSettingRepository.findAll()
			.stream()
			.findFirst();
		if (optionalOneViewSettingEntity.isEmpty()) {
			return OneViewAccountConnectionStatus.DISCONNECTED;
		}

		OneViewSettingEntity findOneViewSettingEntity = optionalOneViewSettingEntity.get();
		if (findOneViewSettingEntity.getConnectionFailedCount() >= 5) {
			return OneViewAccountConnectionStatus.WARNING;
		}

		return OneViewAccountConnectionStatus.CONNECTED;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public String getOneViewSessionToken() {
		OneViewSettingEntity findOneViewSettingEntity = oneViewSettingRepository.findAll().stream().findFirst()
			.orElseThrow(() -> new RestApiException(OneViewErrorCode.DISCONNECTED_ONEVIEW_ACCOUNT));

		try {
			OneViewResDTO.SessionToken sessionToken = issueSessionTokenAPI(
				findOneViewSettingEntity.getApiServerAddress(),
				findOneViewSettingEntity.getApiVersion(),
				findOneViewSettingEntity.getUserName(),
				findOneViewSettingEntity.getPassword());
			updateConnectionFailed(findOneViewSettingEntity, "SUCCESS");
			return sessionToken.getSessionID();
		} catch (SSLException e) {
			throw new RestApiException(OneViewErrorCode.FAILED_SSL_VERIFICATION_MESSAGE);
		} catch (WebClientException e) {
			updateConnectionFailed(findOneViewSettingEntity, "FAIL");
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateConnectionFailed(OneViewSettingEntity findOneViewSettingEntity, String apiCallStatus) {
		if ("SUCCESS".equals(apiCallStatus)) {
			findOneViewSettingEntity.updateConnectionFailed(0);
		} else if ("FAIL".equals(apiCallStatus)) {
			findOneViewSettingEntity.updateConnectionFailed(findOneViewSettingEntity.getConnectionFailedCount() + 1);
		}

		oneViewSettingRepository.save(findOneViewSettingEntity);
	}

	private OneViewResDTO.SessionToken issueSessionTokenAPI(String apiServerAddress,
		Integer apiVersion, String userName, String password) throws SSLException {

		OneViewReqDTO.LoginSession loginSessionReqDTO = OneViewReqDTO.LoginSession.builder()
			.userName(userName)
			.password(password)
			.build();

		// One View 로그인 세션 토큰 발급 API 호출
		return webClientService.postObjectFromUrl(
			apiServerAddress + "/rest/login-sessions",
			Map.of("X-Api-Version", String.valueOf(apiVersion)),
			loginSessionReqDTO,
			OneViewReqDTO.LoginSession.class,
			OneViewResDTO.SessionToken.class
		);
	}
}
