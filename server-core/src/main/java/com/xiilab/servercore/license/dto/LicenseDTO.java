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
	private long id;
	private String version;
	private int gpuCount;
	private LocalDate startDate;
	private LocalDate endDate;
	private LocalDateTime regDate;
}
