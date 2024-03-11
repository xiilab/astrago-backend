package com.xiilab.modulek8s.alertmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.alertmanager.repository.K8sAlertRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class K8sAlertServiceImpl implements K8sAlertService{
	private final K8sAlertRepository k8sAlertRepository;

	@Override
	public void createPrometheusRule(long alertId, List<String> exprList) {
		k8sAlertRepository.createPrometheusRule(alertId, exprList);
	}

	@Override
	public void deletePrometheusRule(long alertId) {
		k8sAlertRepository.deletePrometheusRule(alertId);
	}

	@Override
	public void updatePrometheusRule(long alertId, List<String> exprList) {
		k8sAlertRepository.updatePrometheusRule(alertId, exprList);
	}
	@Override
	public boolean validationCheck(long alertId){
		return k8sAlertRepository.validationCheck(alertId);
	}
}
