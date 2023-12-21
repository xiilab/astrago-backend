package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class VolumeReqDTO {
	private StorageType storageType;
	private VolumeSelectionType volumeSelectionType;
	private String name;    // 볼륨명
	private String volumeMetaDataName;
	private int requestVolume;    // 총 공간
	private String mountPath;    // 마운트할 경로
	private String storageClassMetaName; // 스토리지 클래스 메타데이터 네임

	public JobVolumeVO toJobVolumeVO() {
		return new JobVolumeVO(volumeMetaDataName, mountPath);
	}

	public void setVolumeMetaDataName(String volumeMetaDataName) {
		this.volumeMetaDataName = volumeMetaDataName;
	}
}
