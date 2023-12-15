package com.xiilab.modulemonitor.service;

public interface PrometheusRepository {
	String getRealTimeMetricByQuery(String promql);
	String getHistoryMetricByQuery(String promql, String startDate, String endDate);
}
