package com.xiilab.modulek8s.workload.vo;

public record JobVolumeVO(
	Long id,
	String mountPath,
	String pvName,
	String pvcName,
	String subPath
) {
	public JobVolumeVO(String name, String pvcName, String subPath) {
		this(0L, "", name, pvcName, subPath);
	}
}
