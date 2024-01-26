package com.xiilab.servercore.workload.dto;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.workload.enumeration.TerminalMessageType;

import lombok.Getter;
import lombok.Setter;

/**
 * Terminal DTO로 사용 하는 객체
 * JSON 으로 받아올 데이터 타입
 */
@Getter
@Setter
public class TerminalMessage {
	/**
	 * 메세지 종류
	 */
	private TerminalMessageType type;
	/**
	 * user key press key
	 */
	private String command;
	/**
	 * terminal columns
	 */
	private String columns;
	/**
	 * terminal rows
	 */
	private String rows;
	/**
	 * terminal width
	 */
	private String width;
	/**
	 * terminal  height
	 */
	private String height;
	/**
	 * workspace name
	 */
	private String workspaceName;
	/**
	 * worklaod name
	 */
	private String workloadName;
	/**
	 * workload type(BATCH, INTERACTIVE)
	 */
	private WorkloadType workloadType;
}
