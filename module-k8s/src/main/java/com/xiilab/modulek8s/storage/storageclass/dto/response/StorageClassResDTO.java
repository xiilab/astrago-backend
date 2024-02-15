package com.xiilab.modulek8s.storage.storageclass.dto.response;

import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StorageClassResDTO extends K8SResourceResVO {
	//용량
	private final StorageType storageType;
	private final String ip;
	private final String storageSavePath;

	@Builder
	public StorageClassResDTO(HasMetadata hasMetadata, StorageType storageType, String ip, String storageSavePath) {
		super(hasMetadata);
		this.storageType = storageType;
		this.ip = ip;
		this.storageSavePath = storageSavePath;
	}

	public static StorageClassResDTO toDTO(StorageClass sc) {
		Map<String, String> parameters = sc.getParameters();
		return StorageClassResDTO.builder()
			.hasMetadata(sc)
			.storageType(StorageType.valueOf(sc.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
			.ip(parameters.get("server"))
			.storageSavePath(parameters.get("share"))
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.STORAGE;
	}
}
