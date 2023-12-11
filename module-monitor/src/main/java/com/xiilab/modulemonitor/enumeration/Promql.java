package com.xiilab.modulemonitor.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Promql {
	POD_INFO("kube_pod_info","파드 정보 조회", "POD"),
	NODE_INFO("kube_node_info","노드 정보 조회", "NODE"),
	;

	private final String query;
	private final String description;
	private final String type;

}
