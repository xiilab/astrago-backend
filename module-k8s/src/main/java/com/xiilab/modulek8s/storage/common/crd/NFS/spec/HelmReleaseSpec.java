package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class HelmReleaseSpec {
	private Chart chart;
	private Install install;
	private String interval;
	private String releaseName;
	private String storageNamespace;
	private String targetNamespace;
}
