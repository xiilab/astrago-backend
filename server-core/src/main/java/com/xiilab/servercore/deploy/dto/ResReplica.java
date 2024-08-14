package com.xiilab.servercore.deploy.dto;

import lombok.Getter;

@Getter
public class ResReplica {
	private String name;
	private String replicaResourceName;

	public ResReplica(String name, String replicaResourceName) {
		this.name = name;
		this.replicaResourceName = replicaResourceName;
	}
}
