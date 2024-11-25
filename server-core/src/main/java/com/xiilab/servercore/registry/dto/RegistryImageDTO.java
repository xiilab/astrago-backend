package com.xiilab.servercore.registry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistryImageDTO {
	private final long id;
	private final long projectId;
	private final String name;
	private final String description;
	private final long tagCnt;
	private final long pullCnt;
}
