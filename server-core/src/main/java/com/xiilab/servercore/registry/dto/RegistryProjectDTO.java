package com.xiilab.servercore.registry.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistryProjectDTO {
	private final String id;
	private final String name;
	private final String ownerId;
	private final String ownerName;
	private final LocalDateTime creationDate;
}
