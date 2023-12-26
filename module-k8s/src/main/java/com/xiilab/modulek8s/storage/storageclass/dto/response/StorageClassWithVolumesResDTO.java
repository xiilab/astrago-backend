package com.xiilab.modulek8s.storage.storageclass.dto.response;

import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerStatus;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageClassWithVolumesResDTO extends K8SResourceResVO {
	//용량
	private StorageType storageType;
	private String ip;
	private String storageSavePath;
	private ProvisionerStatus status;
	private List<VolumeDTO> volumes;

	@Builder
	public StorageClassWithVolumesResDTO(HasMetadata hasMetadata, StorageType storageType, String ip, String storageSavePath, List<VolumeDTO> volumes, ProvisionerStatus status) {
		super(hasMetadata);
		this.storageType = storageType;
		this.ip = ip;
		this.storageSavePath = storageSavePath;
		this.volumes = volumes;
		this.status = status;
	}

	public static StorageClassWithVolumesResDTO toDTO(StorageClass sc, List<VolumeDTO> volumes, ProvisionerStatus status){
		Map<String, String> parameters = sc.getParameters();
		return StorageClassWithVolumesResDTO.builder()
			.hasMetadata(sc)
			.storageType(StorageType.valueOf(sc.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
			.ip(parameters.get("server"))
			.storageSavePath(parameters.get("share"))
			.volumes(volumes)
			.status(status)
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.STORAGE;
	}
}
