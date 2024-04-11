package com.xiilab.servercore.workload.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.WorkloadType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkloadLogMessage {
	private WorkloadType workloadType;
	private String workspaceResourceName;
	private String workloadResourceName;

	/**
	 * JSON Text 을 Class 으로 파싱
	 *
	 * @param message
	 * @return
	 */
	public static WorkloadLogMessage convertJsonStringToObject(String message) throws JsonProcessingException {
		try {
			return new ObjectMapper().readValue(message, WorkloadLogMessage.class);
		} catch (JsonProcessingException e) {
			log.error("{}", e);
			throw e;
		}
	}
}
