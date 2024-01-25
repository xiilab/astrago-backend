package com.xiilab.modulemonitor.dto;

import lombok.Builder;

@Builder
public record RequestDTO(String metricName,
						 String startDate,
						 String endDate,
						 String namespace,
						 String podName,
						 String nodeName
) {
}
