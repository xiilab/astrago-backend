package com.xiilab.modulek8s.storage.volume.vo;

import com.xiilab.modulek8s.common.enumeration.*;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import io.fabric8.kubernetes.api.model.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;

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
                .creatorName(createVolumeDTO.getCreatorName())
                .creator(createVolumeDTO.getCreator())
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
                .withName(getUniqueResourceName()) //vo-uuid
                .withNamespace(workspaceMetaDataName)
                .addToAnnotations(createAnnotation())
                .addToLabels(createLabels())
			.build();
	}

	private HashMap<String, String> createLabels() {
		HashMap<String, String> labels = new HashMap<>();
		labels.put(LabelField.CREATOR.getField(), getCreator());
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
		annotation.put(AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorName());
		return annotation;
	}
}
