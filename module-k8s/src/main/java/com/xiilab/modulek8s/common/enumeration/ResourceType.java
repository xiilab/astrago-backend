package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;

@Getter
public enum ResourceType {
	WORKSPACE("ws"),
	WORKLOAD("wl"),
	DEPLOY("deploy"),
	VOLUME("vo"),
	PV("pv"),
	PVC("pvc"),
	STORAGE("st"),
	PLUGIN("pg"),
	RESOURCE_QUOTA("rq"),
	SERVICE("sv"),
	SECRET("sc");


	private String name;

	ResourceType(String name) {
		this.name = name;
	}
}
