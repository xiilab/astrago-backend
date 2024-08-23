package com.xiilab.servercore.deploy.dto;

import lombok.Getter;

@Getter
public class ResReplica {
	private String name;
	private String replicaResourceName;
	private String nodeName;

	public ResReplica(String name, String replicaResourceName, String nodeName) {
		this.name = name;
		this.replicaResourceName = replicaResourceName;
		this.nodeName = nodeName;
	}
}
