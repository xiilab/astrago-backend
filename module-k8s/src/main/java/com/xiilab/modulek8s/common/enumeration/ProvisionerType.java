package com.xiilab.modulek8s.common.enumeration;

import lombok.Getter;

@Getter
public enum ProvisionerType {
	NFS("nfs.csi.k8s.io"),
	NFS1("nfs.csi.k8s.io"),
	PURE("pure");

	private String provisionerName;

	ProvisionerType(String provisionerName) {
		this.provisionerName = provisionerName;
	}
}
