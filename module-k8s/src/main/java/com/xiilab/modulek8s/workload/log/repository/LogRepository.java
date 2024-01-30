package com.xiilab.modulek8s.workload.log.repository;

import io.fabric8.kubernetes.client.dsl.LogWatch;

public interface LogRepository {
	LogWatch watchLogByWorkload(String workspaceName, String podName);
}
