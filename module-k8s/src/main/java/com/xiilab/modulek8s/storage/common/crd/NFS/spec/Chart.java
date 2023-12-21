package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Chart {
	private Spec Spec;

}
