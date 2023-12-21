package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Spec {
	private String chart;
	private String reconcileStrategy;
	private SourceRef sourceRef;
	private String version;
}