package com.xiilab.servercore.license.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
