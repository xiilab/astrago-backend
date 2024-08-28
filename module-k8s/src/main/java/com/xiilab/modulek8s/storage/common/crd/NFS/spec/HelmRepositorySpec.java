package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class HelmRepositorySpec {
	private String url;
	private String interval;

	public HelmRepositorySpec() {
	}

	@Builder
	public HelmRepositorySpec(String url, String interval) {
		this.url = url;
		this.interval = interval;
	}
}
