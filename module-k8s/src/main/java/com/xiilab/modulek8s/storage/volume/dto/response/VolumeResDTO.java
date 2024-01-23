package com.xiilab.modulek8s.storage.volume.dto.response;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VolumeResDTO extends K8SResourceResVO {
	//용량
	private final String requestVolume;
	private final StorageType storageType;

	@Builder
	public VolumeResDTO(HasMetadata hasMetadata, String requestVolume, StorageType storageType) {
		super(hasMetadata);
		this.requestVolume = requestVolume;
		this.storageType = storageType;
	}

	public static VolumeResDTO toDTO(PersistentVolumeClaim pvc) {
		return VolumeResDTO.builder()
			.hasMetadata(pvc)
			.requestVolume(pvc.getSpec().getResources().getRequests().get("storage").toString())
			.storageType(StorageType.valueOf(pvc.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.VOLUME;
	}
}
