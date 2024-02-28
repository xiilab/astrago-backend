package com.xiilab.modulemonitor.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulemonitor.repository.K8sAlertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class K8sAlertServiceImpl implements K8sAlertService{
	private final K8sAlertRepository k8sAlertRepository;

	@Override
	public void createPrometheusRule(long alertId, List<String> exprList) {
		k8sAlertRepository.createPrometheusRule(alertId, exprList);
	}
}
