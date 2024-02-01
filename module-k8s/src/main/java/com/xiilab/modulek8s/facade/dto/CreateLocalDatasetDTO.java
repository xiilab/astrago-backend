package com.xiilab.modulek8s.facade.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateLocalDatasetDTO {
	private String datasetName;
	private String ip;
	private String storagePath;
	private String namespace;
	private String dockerImage;
	private String hostPath;
}