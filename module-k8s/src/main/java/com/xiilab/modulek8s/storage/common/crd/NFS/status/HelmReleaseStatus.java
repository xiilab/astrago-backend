package com.xiilab.modulek8s.storage.common.crd.NFS.status;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class HelmReleaseStatus {
	private List<Conditions> conditions;
	private String helmChart;
	private List<History> history;

	public HelmReleaseStatus() {

	}

	@Builder
	public HelmReleaseStatus(List<Conditions> conditions, String helmChart, List<History> history) {
		this.conditions = conditions;
		this.helmChart = helmChart;
		this.history = history;
	}
}
