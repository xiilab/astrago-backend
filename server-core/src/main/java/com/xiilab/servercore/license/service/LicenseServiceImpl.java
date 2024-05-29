package com.xiilab.servercore.license.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LicenseErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.moduleuser.service.UserService;
import com.xiilab.servercore.license.dto.LicenseDTO;
import com.xiilab.servercore.license.entity.LicenseEntity;
import com.xiilab.servercore.license.repository.LicenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
	private final LicenseRepository licenseRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final MailService mailService;
	private final UserService userService;

	@Override
	@Transactional(readOnly = true)
	public Page<LicenseDTO> getLicenseHistory(Pageable pageable) {
		Page<LicenseEntity> licenseList = licenseRepository.findAll(pageable);
		return licenseList.map(LicenseEntity::decryptLicensekey);
	}

	@Override
	@Transactional
	public void registerLicense(String licenseKey) {
		licenseRepository.save(new LicenseEntity(licenseKey));
	}

	@Override
	public void checkLicense() {
		LicenseEntity recentlyLicense = licenseRepository.findTopByOrderByRegDateDesc();
		if (recentlyLicense == null) {
			throw new RestApiException(LicenseErrorCode.NOT_FOUND_LICENSE_KEYS);
		}
		try {
			recentlyLicense.checkLicense();
		} catch (RestApiException e) {
			LicenseDTO licenseDTO = recentlyLicense.decryptLicensekey();
			// 회원가입 알림 메시지 발송
			AlertMessage licenseExpiration = AlertMessage.LICENSE_EXPIRATION;
			String mailTitle = licenseExpiration.getMailTitle();
			String title = licenseExpiration.getTitle();
			String message = String.format(licenseExpiration.getMessage(), licenseDTO.getEndDate());

			MailDTO mailDTO = MailServiceUtils.licenseMail(licenseDTO.getEndDate());
			eventPublisher.publishEvent(
				new AdminAlertEvent(AlertName.ADMIN_LICENSE_EXPIRATION, null, mailTitle, title, message, null, mailDTO));
			throw e;
		}
	}

	@Override
	public LicenseDTO getRecentlyLicenseInfo() {
		LicenseEntity license = licenseRepository.findTopByOrderByRegDateDesc();
		return license.decryptLicensekey();
	}

}
