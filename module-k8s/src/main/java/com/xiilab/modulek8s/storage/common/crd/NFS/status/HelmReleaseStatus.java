package com.xiilab.modulek8s.storage.common.crd.NFS.status;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class HelmReleaseStatus {
	private List<Conditions> conditions;
	private String helmChart;
	private List<History> history;
}
