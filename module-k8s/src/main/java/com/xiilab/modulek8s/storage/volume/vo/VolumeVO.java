package com.xiilab.modulek8s.storage.volume.vo;

import java.time.LocalDateTime;
import java.util.HashMap;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.common.enumeration.AccessMode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class VolumeVO extends K8SResourceReqVO {
	private StorageType storageType;
	private String workspaceMetaDataName;
	private String storageClassMetaName;
	private int requestVolume;

	@Override
	public HasMetadata createResource() {
		return new PersistentVolumeClaimBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	public static VolumeVO dtoToVo(CreateVolumeDTO createVolumeDTO) {
		return VolumeVO.builder()
			.name(createVolumeDTO.getName())
			.createdAt(LocalDateTime.now())
			.creatorUserName(createVolumeDTO.getCreatorUserName())
			.creatorId(createVolumeDTO.getCreatorId())
			.workspaceMetaDataName(createVolumeDTO.getWorkspaceMetaDataName())
			.storageClassMetaName(createVolumeDTO.getStorageClassMetaName())
			.requestVolume(createVolumeDTO.getRequestVolume())
			.storageType(createVolumeDTO.getStorageType())
			.build();
	}
	private PersistentVolumeClaimSpec createSpec() {
		return new PersistentVolumeClaimSpecBuilder()
			.withStorageClassName(storageClassMetaName) //st-uuid
			.withAccessModes(AccessMode.RWM.getAccessMode())
			.withNewResources()
			.addToRequests("storage", new Quantity(requestVolume + "Gi"))
			.endResources()
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.VOLUME;
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getUniqueJobName()) //vo-uuid
			.withNamespace(workspaceMetaDataName)
			.addToAnnotations(createAnnotation())
			.addToLabels(createLabels())
			.build();
	}

	private HashMap<String, String> createLabels() {
		HashMap<String, String> labels = new HashMap<>();
		labels.put(LabelField.CREATOR_ID.getField(), getCreatorId());
		labels.put(LabelField.STORAGE_TYPE.getField(), storageType.name());
		labels.put(LabelField.CONTROL_BY.getField(), "astra");
		labels.put(LabelField.STORAGE_NAME.getField(), storageClassMetaName);
		return labels;
	}

	private HashMap<String, String> createAnnotation() {
		HashMap<String, String> annotation = new HashMap<>();
		annotation.put(AnnotationField.NAME.getField(), getName());
		annotation.put(AnnotationField.DESCRIPTION.getField(), getDescription());
		annotation.put(AnnotationField.CREATED_AT.getField(), String.valueOf(getCreatedAt()));
		annotation.put(AnnotationField.CREATOR_USER_NAME.getField(), getCreatorUserName());
		return annotation;
	}
}
