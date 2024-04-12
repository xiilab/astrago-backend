package com.xiilab.servercore.license.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.xiilab.servercore.license.entity.LicenseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LicenseDTO {
	protected long id;
	protected String version;
	protected int gpuCount;
	protected LocalDate startDate;
	protected LocalDate endDate;
	protected LocalDateTime regDate;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LicenseInfoDTO extends LicenseDTO {
		private String key;

		public LicenseInfoDTO(LicenseEntity licenseEntity) {
			super(licenseEntity.getId(), licenseEntity.getVersion(), licenseEntity.getGpuCount(), licenseEntity.getStartDate(), licenseEntity.getEndDate(), licenseEntity.getRegDate());
			this.key = licenseEntity.getLicenseKey();
		}
	}
}
