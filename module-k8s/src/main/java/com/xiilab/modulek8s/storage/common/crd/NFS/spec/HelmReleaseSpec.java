package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class HelmReleaseSpec {
	private Chart chart;
	private Install install;
	private String interval;
	private String releaseName;
	private String storageNamespace;
	private String targetNamespace;
	private Map<String, Object> values;
	public HelmReleaseSpec() {
	}
	@Builder
	public HelmReleaseSpec(Chart chart, Install install, String interval, String releaseName, String storageNamespace,
		String targetNamespace, Map<String, Object> values) {
		this.chart = chart;
		this.install = install;
		this.interval = interval;
		this.releaseName = releaseName;
		this.storageNamespace = storageNamespace;
		this.targetNamespace = targetNamespace;
		this.values = values;
	}
}
