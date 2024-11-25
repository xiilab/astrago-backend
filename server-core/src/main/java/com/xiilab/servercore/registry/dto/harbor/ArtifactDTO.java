package com.xiilab.servercore.registry.dto.harbor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ArtifactDTO {
	@JsonProperty("id")
	private int id;

	@JsonProperty("type")
	private String type;

	@JsonProperty("media_type")
	private String mediaType;

	@JsonProperty("manifest_media_type")
	private String manifestMediaType;

	@JsonProperty("artifact_type")
	private String artifactType;

	@JsonProperty("project_id")
	private int projectId;

	@JsonProperty("repository_id")
	private int repositoryId;

	@JsonProperty("repository_name")
	private String repositoryName;

	@JsonProperty("digest")
	private String digest;

	@JsonProperty("size")
	private long size;

	@JsonProperty("icon")
	private String icon;

	@JsonProperty("push_time")
	private LocalDateTime pushTime;

	@JsonProperty("pull_time")
	private LocalDateTime pullTime;

	@JsonProperty("extra_attrs")
	private Map<String, Object> extraAttrs;

	@JsonProperty("annotations")
	private Map<String, String> annotations;

	@JsonProperty("references")
	private List<Reference> references;

	@JsonProperty("tags")
	private List<Tag> tags;

	@JsonProperty("addition_links")
	private Map<String, Link> additionLinks;

	@JsonProperty("labels")
	private List<Label> labels;

	@JsonProperty("scan_overview")
	private Map<String, ScanOverview> scanOverview;

	@JsonProperty("sbom_overview")
	private SBOMOverview sbomOverview;

	@JsonProperty("accessories")
	private List<Accessory> accessories;

	// **Inner Classes**
	@Data
	public static class Reference {
		@JsonProperty("parent_id")
		private int parentId;

		@JsonProperty("child_id")
		private int childId;

		@JsonProperty("child_digest")
		private String childDigest;

		@JsonProperty("platform")
		private Platform platform;

		@JsonProperty("annotations")
		private Map<String, String> annotations;

		@JsonProperty("urls")
		private List<String> urls;
	}

	@Data
	public static class Platform {
		@JsonProperty("architecture")
		private String architecture;

		@JsonProperty("os")
		private String os;

		@JsonProperty("'os.version'")
		private String osVersion;

		@JsonProperty("'os.features'")
		private List<String> osFeatures;

		@JsonProperty("variant")
		private String variant;
	}

	@Data
	public static class Tag {
		@JsonProperty("id")
		private int id;

		@JsonProperty("repository_id")
		private int repositoryId;

		@JsonProperty("artifact_id")
		private int artifactId;

		@JsonProperty("name")
		private String name;

		@JsonProperty("push_time")
		private LocalDateTime pushTime;

		@JsonProperty("pull_time")
		private LocalDateTime pullTime;

		@JsonProperty("immutable")
		private boolean immutable;
	}

	@Data
	public static class Link {
		@JsonProperty("href")
		private String href;

		@JsonProperty("absolute")
		private boolean absolute;
	}

	@Data
	public static class Label {
		@JsonProperty("id")
		private int id;

		@JsonProperty("name")
		private String name;

		@JsonProperty("description")
		private String description;

		@JsonProperty("color")
		private String color;

		@JsonProperty("scope")
		private String scope;

		@JsonProperty("project_id")
		private int projectId;

		@JsonProperty("creation_time")
		private LocalDateTime creationTime;

		@JsonProperty("update_time")
		private LocalDateTime updateTime;
	}

	@Data
	public static class ScanOverview {
		@JsonProperty("report_id")
		private String reportId;

		@JsonProperty("scan_status")
		private String scanStatus;

		@JsonProperty("severity")
		private String severity;

		@JsonProperty("duration")
		private int duration;

		@JsonProperty("summary")
		private Summary summary;

		@JsonProperty("start_time")
		private LocalDateTime startTime;

		@JsonProperty("end_time")
		private LocalDateTime endTime;

		@JsonProperty("complete_percent")
		private int completePercent;

		@JsonProperty("scanner")
		private Scanner scanner;
	}

	@Data
	public static class Summary {
		@JsonProperty("total")
		private int total;

		@JsonProperty("fixable")
		private int fixable;

		@JsonProperty("summary")
		private Map<String, Integer> summary;
	}

	@Data
	public static class Scanner {
		@JsonProperty("name")
		private String name;

		@JsonProperty("vendor")
		private String vendor;

		@JsonProperty("version")
		private String version;
	}

	@Data
	public static class SBOMOverview {
		@JsonProperty("start_time")
		private LocalDateTime startTime;

		@JsonProperty("end_time")
		private LocalDateTime endTime;

		@JsonProperty("scan_status")
		private String scanStatus;

		@JsonProperty("sbom_digest")
		private String sbomDigest;

		@JsonProperty("report_id")
		private String reportId;

		@JsonProperty("duration")
		private int duration;

		@JsonProperty("scanner")
		private Scanner scanner;
	}

	@Data
	public static class Accessory {
		@JsonProperty("id")
		private int id;

		@JsonProperty("artifact_id")
		private int artifactId;

		@JsonProperty("subject_artifact_id")
		private int subjectArtifactId;

		@JsonProperty("subject_artifact_digest")
		private String subjectArtifactDigest;

		@JsonProperty("subject_artifact_repo")
		private String subjectArtifactRepo;

		@JsonProperty("size")
		private long size;

		@JsonProperty("digest")
		private String digest;

		@JsonProperty("type")
		private String type;

		@JsonProperty("icon")
		private String icon;

		@JsonProperty("creation_time")
		private LocalDateTime creationTime;
	}
}
