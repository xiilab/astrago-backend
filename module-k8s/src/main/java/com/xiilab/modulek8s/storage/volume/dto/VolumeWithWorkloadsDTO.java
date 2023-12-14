package com.xiilab.modulek8s.storage.volume.dto;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public class VolumeWithWorkloadsDTO extends K8SResourceResVO {
	//workload  list
	List<String> workloadNames;
	//용량
	private String requestVolume;

	@Builder
	public VolumeWithWorkloadsDTO(HasMetadata hasMetadata, List<String> workloadNames, String requestVolume) {
		super(hasMetadata);
		this.requestVolume = requestVolume;
		this.workloadNames = workloadNames;
	}

	@Override
	protected ResourceType getType() {
		return null;
	}
}
