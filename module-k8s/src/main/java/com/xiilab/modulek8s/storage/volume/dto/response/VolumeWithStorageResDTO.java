package com.xiilab.modulek8s.storage.volume.dto.response;

import java.util.List;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VolumeWithStorageResDTO extends K8SResourceResVO {
	private String workspaceName;
	//workload  list
	private List<String> workloadNames;
	//용량
	private String requestVolume;
	private StorageType storageType;
	private String storageClassName;
	private String savePath;

	@Builder
	public VolumeWithStorageResDTO(HasMetadata hasMetadata, String workspaceName, List<String> workloadNames,
		String requestVolume, StorageType storageType, String storageClassName, String savePath) {
		super(hasMetadata);
		this.workspaceName = workspaceName;
		this.workloadNames = workloadNames;
		this.requestVolume = requestVolume;
		this.storageType = storageType;
		this.storageClassName = storageClassName;
		this.savePath = savePath;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.VOLUME;
	}
}
