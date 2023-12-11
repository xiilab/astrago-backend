package com.xiilab.modulecommon.dto;

import java.time.LocalDateTime;

public record ResponseDTO(
	int status,
	LocalDateTime timestamp,
	String message,
	String details
) {
}
