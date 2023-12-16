package com.xiilab.modulek8s.storage.volume.dto.response;

import java.util.List;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VolumeWithWorkloadsResDTO extends K8SResourceResVO {
	//workload  list
	List<String> workloadNames;
	//용량
	private String requestVolume;
	private StorageType storageType;

	@Builder
	public VolumeWithWorkloadsResDTO(HasMetadata hasMetadata, List<String> workloadNames, String requestVolume, StorageType storageType) {
		super(hasMetadata);
		this.requestVolume = requestVolume;
		this.workloadNames = workloadNames;
		this.storageType = storageType;
	}

	@Override
	protected ResourceType getType() {
		return null;
	}
}
