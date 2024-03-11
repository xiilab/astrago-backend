package com.xiilab.modulek8s.alertmanager.repository;

import java.util.List;

public interface K8sAlertRepository {
	void createPrometheusRule(long alertId, List<String> exprList);
	void deletePrometheusRule(long alertId);
	void updatePrometheusRule(long alertId, List<String> exprList);
	boolean validationCheck(long alertId);
}
