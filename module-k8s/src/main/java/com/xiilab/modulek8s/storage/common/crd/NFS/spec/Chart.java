package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Chart {
	private Spec Spec;

	public Chart() {
	}

	@Builder
	public Chart(com.xiilab.modulek8s.storage.common.crd.NFS.spec.Spec spec) {
		Spec = spec;
	}
}
