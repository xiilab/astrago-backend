package com.xiilab.modulek8s.workload.svc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SvcType {
	CLUSTER_IP("ClusterIP"),
	NODE_PORT("NodePort"),
	LOAD_BALANCER("LoadBalancer");

	private final String type;
}
