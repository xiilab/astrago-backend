package com.xiilab.modulecommon.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ResponseDTO(
	int status,
	LocalDateTime timestamp,
	String message,
	String details
) {
}
