package com.xiilab.modulemonitor.service;

import java.util.List;

public interface K8sAlertService {
	void createPrometheusRule(long alertId, List<String> exprList);
	void deletePrometheusRule(long alertId);
	void updatePrometheusRule(long alertId, List<String> exprList);
}
