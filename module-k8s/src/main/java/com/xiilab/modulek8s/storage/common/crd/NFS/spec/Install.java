package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Install {
	private boolean createNamespace;

	public Install() {
	}

	@Builder
	public Install(boolean createNamespace) {
		this.createNamespace = createNamespace;
	}
}
