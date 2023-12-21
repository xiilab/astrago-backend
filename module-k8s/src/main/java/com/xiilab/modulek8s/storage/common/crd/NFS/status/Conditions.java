package com.xiilab.modulek8s.storage.common.crd.NFS.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Conditions {
	private String lastTransitionTime;
	private String message;
	private String observedGeneration;
	private String reason;
	private String status;
	private String type;
}
