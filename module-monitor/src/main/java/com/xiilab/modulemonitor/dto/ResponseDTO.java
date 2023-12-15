package com.xiilab.modulemonitor.dto;

import java.util.List;

import lombok.Builder;

public record ResponseDTO() {

	@Builder
	public record RealTimeDTO(String metricName,
							  String dateTime,
							  String nameSpace,
							  String nodeName,
							  String kubeNodeName,
							  String podName,
							  String instance,
							  String modelName,
							  String value){
	}
	@Builder
	public record HistoryDTO(String metricName,
							 String nameSpace,
							 String nodeName,
							 String podName,
							 String instance,
							 List<ValueDTO> valueDTOS
	){

	}
	@Builder
	public record ValueDTO(String dateTime,
						   String value){
	}

	@Builder
	public record PromqlDTO(String metricName,
							String description,
							String type
	){
	}
	@Builder
	public record EventDTO(String type,
						   String workloadName,
						   String time,
						   String reason,
						   String message){
	}
}
