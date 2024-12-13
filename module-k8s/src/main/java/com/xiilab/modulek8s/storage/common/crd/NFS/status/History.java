package com.xiilab.modulek8s.storage.common.crd.NFS.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class History {
	private String chartName;
	private String chartVersion;
	private String configDigest;
	private String digest;
	private String firstDeployed;
	private String lastDeployed;
	private String name;
	private String namespace;
	private String status;
	private String version;
}
