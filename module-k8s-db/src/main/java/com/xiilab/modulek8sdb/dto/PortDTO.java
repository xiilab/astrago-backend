package com.xiilab.modulek8sdb.dto;

import lombok.Getter;

@Getter
public class PortDTO {
	private String name;
	private int portNum;
	private int targetPortNum;
}
