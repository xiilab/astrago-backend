package com.xiilab.servercore.license.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.servercore.license.dto.LicenseDTO;

public interface LicenseService {
	Page<LicenseDTO> getLicenseHistory(Pageable pageable);

	void registerLicense(String licenseKey);

	void checkLicense();

	LicenseDTO getRecentlyLicenseInfo();
}
