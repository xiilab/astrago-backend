package com.xiilab.modulek8s.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DistributedJobRole {
	LAUNCHER("Launcher"),
	WORKER("Worker");

	private final String name;
}
