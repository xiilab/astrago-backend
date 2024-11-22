package com.xiilab.servercore.registry.dto.harbor;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDTO {

	@Getter
	@AllArgsConstructor
	public static class ResDTO {
		@JsonProperty("project_id")
		private final String id;
		private final String name;
		@JsonProperty("owner_id")
		private final String ownerId;
		@JsonProperty("owner_name")
		private final String ownerName;
		@JsonProperty("creation_time")
		private final LocalDateTime creationDate;
		@JsonProperty("update_time")
		private final LocalDateTime updateDate;
		private final Map<String, String> metadata;
		@JsonProperty("repo_count")
		private final Long repoCnt;
	}

	@Getter
	@AllArgsConstructor
	public static class SummaryDTO {

		@JsonProperty("repo_count")
		private int repoCount;

		@JsonProperty("project_admin_count")
		private int projectAdminCount;

		@JsonProperty("maintainer_count")
		private int maintainerCount;

		@JsonProperty("developer_count")
		private int developerCount;

		@JsonProperty("guest_count")
		private int guestCount;

		@JsonProperty("limited_guest_count")
		private int limitedGuestCount;

		@JsonProperty("quota")
		private Quota quota;

		@JsonProperty("registry")
		private Registry registry;

		@Getter
		@AllArgsConstructor
		public static class Quota {
			@JsonProperty("hard")
			private Map<String, String> hard;

			@JsonProperty("used")
			private Map<String, String> used;
		}

		@Getter
		@AllArgsConstructor
		public static class Registry {
			@JsonProperty("id")
			private int id;

			@JsonProperty("url")
			private String url;

			@JsonProperty("name")
			private String name;

			@JsonProperty("credential")
			private Credential credential;

			@JsonProperty("type")
			private String type;

			@JsonProperty("insecure")
			private boolean insecure;

			@JsonProperty("description")
			private String description;

			@JsonProperty("status")
			private String status;

			@JsonProperty("creation_time")
			private String creationTime;

			@JsonProperty("update_time")
			private String updateTime;

			@Getter
			@AllArgsConstructor
			public static class Credential {
				@JsonProperty("type")
				private String type;

				@JsonProperty("access_key")
				private String accessKey;

				@JsonProperty("access_secret")
				private String accessSecret;
			}
		}
	}
}
