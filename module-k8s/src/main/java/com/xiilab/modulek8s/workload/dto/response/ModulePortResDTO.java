package com.xiilab.modulek8s.workload.dto.response;

import lombok.Builder;

@Builder
public record ModulePortResDTO(
	String name,
	Integer originPort,
	String url) {
}
