package com.xiilab.modulek8s.storage.volume.dto.response;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class VolumeWithWorkloadsResDTO extends K8SResourceResVO {
	private String workspaceName;
	//workload  list
	private List<String> workloadNames;
	//용량
	private String requestVolume;
	private StorageType storageType;
	private String storageClassName;

	@Builder
	public VolumeWithWorkloadsResDTO(HasMetadata hasMetadata, String workspaceName, List<String> workloadNames, String requestVolume, StorageType storageType, String storageClassName) {
		super(hasMetadata);
		this.workspaceName = workspaceName;
		this.requestVolume = requestVolume;
		this.workloadNames = workloadNames;
		this.storageType = storageType;
		this.storageClassName = storageClassName;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.VOLUME;
	}
}
