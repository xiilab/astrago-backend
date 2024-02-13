package com.xiilab.modulek8s.workload.vo;

public record JobVolumeVO(
	Long id,
	String mountPath,
	String pvName,
	String pvcName
) {
	public JobVolumeVO(String name, String pvcName) {
		this(0L, "", name, pvcName);
	}
}
