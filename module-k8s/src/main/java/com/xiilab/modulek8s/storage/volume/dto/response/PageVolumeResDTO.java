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
public class PageVolumeResDTO extends K8SResourceResVO {
	//용량
	private String requestVolume;
	private StorageType storageType;
	private boolean isUsed;

	@Builder
	public PageVolumeResDTO(HasMetadata hasMetadata, String requestVolume, StorageType storageType, boolean isUsed) {
		super(hasMetadata);
		this.requestVolume = requestVolume;
		this.storageType = storageType;
		this.isUsed = isUsed;
	}

	@Override
	protected ResourceType getType() {
		return null;
	}

	public static PageVolumeResDTO toDTO(PersistentVolumeClaim pvc){
		return PageVolumeResDTO.builder()
			.hasMetadata(pvc)
			.requestVolume(pvc.getSpec().getResources().getRequests().get("storage").toString())
			.storageType(StorageType.valueOf(pvc.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
			.build();
	}
	public void setIsUsed(boolean isUsed){
		this.isUsed = isUsed;
	}
}
