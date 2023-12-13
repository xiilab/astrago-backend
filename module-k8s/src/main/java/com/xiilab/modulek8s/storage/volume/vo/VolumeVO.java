package com.xiilab.modulek8s.storage.volume.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.enums.AccessMode;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VolumeVO extends K8SResourceReqVO {
	private String workspaceMetaDataName;
	private String storageClassMetaName;
	private int requestVolume;

	@Builder
	public VolumeVO(String resourceName, String name, String description, LocalDateTime createdAt,
		String creatorName, String creator, String workspaceMetaDataName, String storageClassMetaName,
		int requestVolume) {
		super(resourceName, name, description, createdAt, creatorName, creator);
		this.workspaceMetaDataName = workspaceMetaDataName;
		this.storageClassMetaName = storageClassMetaName;
		this.requestVolume = requestVolume;
	}

	@Override
	public HasMetadata createResource() {

		return new PersistentVolumeClaimBuilder()
			.withNewMetadata()
			.withName(getResourceName()) //vo-uuid
			.withNamespace(workspaceMetaDataName)
			.addToAnnotations(createAnnotation())
			.addToLabels(createLabels())
			.endMetadata()
			.withNewSpec()
			.withStorageClassName(storageClassMetaName) //st-uuid
			.withAccessModes(AccessMode.RWM.getAccessMode())
			.withNewResources()
			.addToRequests("storage", new Quantity(requestVolume + "Gi"))
			.endResources()
			.endSpec()
			.build();
	}

	private HashMap<String, String> createLabels() {
		HashMap<String, String> labels = new HashMap<>();
		labels.put(LabelField.CREATOR.getField(), getCreator());
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

	@Override
	protected ObjectMeta createMeta() {
		return null;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.VOLUME;
	}

	public static VolumeVO dtoToVo(CreateVolumeDTO createVolumeDTO){
		return VolumeVO.builder()
			.name(createVolumeDTO.getName())
			.createdAt(LocalDateTime.now())
			.creatorName("이용춘")//keycloak
			.creator("yc.lee")//keycloak
			.workspaceMetaDataName(createVolumeDTO.getWorkspaceMetaDataName())
			.storageClassMetaName(createVolumeDTO.getStorageClassMetaName())
			.requestVolume(createVolumeDTO.getRequestVolume())
			.build();
	}

}
