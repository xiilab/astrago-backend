package com.xiilab.modulek8s.storage.common.crd.NFS.spec;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SourceRef {
	private String kind;
	private String name;
}
