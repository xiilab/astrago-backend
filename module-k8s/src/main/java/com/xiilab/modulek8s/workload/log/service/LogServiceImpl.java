package com.xiilab.modulek8s.workload.log.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.log.repository.LogRepository;

import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogServiceImpl implements LogService{
	private final LogRepository logRepository;

	@Override
	public LogWatch watchLogByWorkload(String workspaceName, String podName) {
		return logRepository.watchLogByWorkload(workspaceName, podName);
	}

	@Override
	public String getWorkloadLogByWorkloadName(String namespace, String podName) {
		return logRepository.getWorkloadLogByWorkloadName(namespace,podName);
	}
}
