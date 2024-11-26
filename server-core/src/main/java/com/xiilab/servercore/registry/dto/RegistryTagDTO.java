package com.xiilab.servercore.registry.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistryTagDTO {
	private long id;
	private long projectId;
	private long repositoryId;
	private String name;
	private long size;
	private String digest;
	private LocalDateTime pushTime;
	private LocalDateTime pullTime;
	private String tag;
}
