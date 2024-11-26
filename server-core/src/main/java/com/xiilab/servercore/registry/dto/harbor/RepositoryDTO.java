package com.xiilab.servercore.registry.dto.harbor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class RepositoryDTO {

	@Getter
	@AllArgsConstructor
	public static class ResDTO {
		private final long id;
		@JsonProperty("project_id")
		private final long projectId;
		@JsonProperty("name")
		private final String name;
		@JsonProperty("description")
		private final String description;
		@JsonProperty("artifact_count")
		private final long artifactCnt;
		@JsonProperty("pull_count")
		private final long pullCnt;
		@JsonProperty("creation_time")
		private final LocalDateTime creationTime;
		@JsonProperty("update_time")
		private final LocalDateTime updateTime;
	}
}
