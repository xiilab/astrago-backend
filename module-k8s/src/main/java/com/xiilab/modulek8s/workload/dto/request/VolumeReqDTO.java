package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;
import lombok.Builder;

@Builder
public record VolumeReqDTO(
	StorageType storageType,
	VolumeSelectionType volumeSelectionType,
	String name, 	// 볼륨명
	String volumeMetaDataName,
	int requestVolume, 	// 총 공간
	String mountPath	// 마운트할 경로
) {
	public JobVolumeVO toJobVolumeVO() {
		return new JobVolumeVO(volumeMetaDataName, mountPath);
	}
}
