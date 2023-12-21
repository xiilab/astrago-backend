package com.xiilab.modulek8s.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceType {
	CLUSTER_IP("ClusterIP"),
	NODE_PORT("NodePort"),
	LOAD_BALANCER("LoadBalancer");

	private final String type;
}
