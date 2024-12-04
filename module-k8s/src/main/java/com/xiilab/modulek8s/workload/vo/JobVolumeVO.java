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
	
	// je.kim my-disk 용도의 생성자
	public JobVolumeVO(String name, String pvcName, String mountPath , String subPath) {
		this(0L, mountPath , name, pvcName, subPath);
	}
}
