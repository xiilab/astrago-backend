package com.xiilab.modulemonitor.dto;

import lombok.Builder;

public record ResponseDTO() {

	@Builder
	public record RealTimeDTO(String metricName,
							  String dateTime,
							  String nameSpace,
							  String nodeName,
							  String podName,
							  String instance,
							  String value){
	}
}
