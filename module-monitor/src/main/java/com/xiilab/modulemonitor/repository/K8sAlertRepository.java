package com.xiilab.modulemonitor.repository;

import java.util.List;

public interface K8sAlertRepository {
	void createPrometheusRule(long alertId, List<String> exprList);
}
