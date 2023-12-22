package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Spec {
	private String chart;
	private SourceRef sourceRef;

	public Spec() {
	}

	@Builder
	public Spec(String chart, SourceRef sourceRef) {
		this.chart = chart;
		this.sourceRef = sourceRef;
	}
}