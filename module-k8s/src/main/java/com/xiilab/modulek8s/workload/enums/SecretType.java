package com.xiilab.modulek8s.workload.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecretType {
	DOCKER_HUB("kubernetes.io/dockerconfigjson");

	private final String type;
}
