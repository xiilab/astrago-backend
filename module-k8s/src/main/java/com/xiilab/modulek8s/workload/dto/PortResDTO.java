package com.xiilab.modulek8s.workload.dto;

public record PortResDTO(
	String name,
	String originPort,
	String nodePort
) {
}
