package com.xiilab.modulemonitor.dto;

public record RequestDTO(String metricName,
						 String startDate,
						 String endDate,
						 String podName,
						 String nodeName,
						 String clusterIP
) {
}
