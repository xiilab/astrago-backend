package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
public class ModuleVolumeReqDTO {
	private StorageType storageType;
	private VolumeSelectionType volumeSelectionType;
	private String name; 	// 볼륨명
	private String volumeMetaDataName;
	private int requestVolume; 	// 총 공간
	private String mountPath;	// 마운트할 경로
	private String storageClassMetaName; // 스토리지 클래스 메타데이터 네임

	public ModuleVolumeReqDTO(StorageType storageType, VolumeSelectionType volumeSelectionType, String name,
		String volumeMetaDataName, int requestVolume, String mountPath, String storageClassMetaName) {
		this.storageType = storageType;
		this.volumeSelectionType = volumeSelectionType;
		this.name = name;
		this.volumeMetaDataName = volumeMetaDataName;
		this.requestVolume = requestVolume;
		this.mountPath = mountPath;
		this.storageClassMetaName = storageClassMetaName;
	}

	public JobVolumeVO toJobVolumeVO() {
		return new JobVolumeVO(volumeMetaDataName, mountPath);
	}
	public void setVolumeMetaDataName(String volumeMetaDataName) {
		this.volumeMetaDataName = volumeMetaDataName;
	}
}
