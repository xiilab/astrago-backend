package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SourceRef {
	private String kind;
	private String name;

	public SourceRef() {
	}
	@Builder
	public SourceRef(String kind, String name) {
		this.kind = kind;
		this.name = name;
	}
}
