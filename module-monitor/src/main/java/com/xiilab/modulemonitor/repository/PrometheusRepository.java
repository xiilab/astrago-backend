package com.xiilab.modulemonitor.repository;

public interface PrometheusRepository {
	String getRealTimeMetricByQuery(String promql);

	String getHistoryMetricByQuery(String promql, String startDate, String endDate);

	String getHistoryMetricByQuery(String promql, String startDate, String endDate, long step);

	// String getHistoryMetricByQuery(String promql, String endDate, long step);
}
