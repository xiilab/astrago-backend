package com.xiilab.modulek8s.common.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class K8SResourceMetadataDTO {
	private String name;
	private String description;
	private String resourceName;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private String workspaceName;
	private String workspaceResourceName;
	private String creatorId;
	private String creatorUserName;
	private String creatorFullName;
	private Integer cpuReq;
	private Integer gpuReq;
	private Integer memReq;
	private String imgName;
	private String imgTag;
	private String datasetIds;
	private String modelIds;
	private String codeIds;
}
