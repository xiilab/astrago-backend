package com.xiilab.servercore.license.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LicenseErrorCode;
import com.xiilab.servercore.license.dto.LicenseDTO;
import com.xiilab.servercore.license.entity.LicenseEntity;
import com.xiilab.servercore.license.repository.LicenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LicenseServiceImpl implements LicenseService {
	private final LicenseRepository licenseRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<LicenseDTO> getLicenseHistory(Pageable pageable) {
		Page<LicenseEntity> licenseList = licenseRepository.findAll(pageable);
		return licenseList.map(license ->
			LicenseDTO.builder()
				.id(license.getId())
				.version(license.getVersion())
				.regDate(license.getRegDate())
				.startDate(license.getStartDate())
				.endDate(license.getEndDate())
				.gpuCount(license.getGpuCount())
				.build());
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
		recentlyLicense.checkLicense();
	}

}
