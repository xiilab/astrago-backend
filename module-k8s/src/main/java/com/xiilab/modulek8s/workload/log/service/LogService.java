package com.xiilab.modulek8s.workload.log.service;

import io.fabric8.kubernetes.client.dsl.LogWatch;

public interface LogService {
	LogWatch watchLogByWorkload(String workspaceId, String workloadId);
	String getWorkloadLogByWorkloadName(String namespace, String podName);
}
