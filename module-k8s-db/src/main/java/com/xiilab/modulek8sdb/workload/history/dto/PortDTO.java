package com.xiilab.modulek8sdb.workload.history.dto;

import lombok.Getter;

@Getter
public class PortDTO {
	private String name;
	private int portNum;
	private int targetPortNum;
}
