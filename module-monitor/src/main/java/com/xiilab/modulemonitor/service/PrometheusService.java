package com.xiilab.modulemonitor.service;

public interface PrometheusService {
	String getRealTimeMetricByQuery(String promql);
	String getHistoryMetricByQuery(String promql, String startDate, String endDate);
}
