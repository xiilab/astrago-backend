package com.xiilab.modulek8s.storage.storageclass.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerType;
import com.xiilab.modulek8s.common.enumeration.ReclaimPolicyType;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.storage.StorageClassBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StorageClassVO extends K8SResourceReqVO {
	private StorageType storageType;
	private ProvisionerType provisioner;
	private ReclaimPolicyType reclaimPolicy;
	private Map<String, String> parameters;

	public static StorageClassVO dtoToVo(CreateStorageClassDTO createStorageClassDTO) {
		return StorageClassVO.builder()
			.name(createStorageClassDTO.getName())
			.description(createStorageClassDTO.getDescription())
			.storageType(createStorageClassDTO.getStorageType())
			.provisioner(ProvisionerType.valueOf(createStorageClassDTO.getStorageType().name()))
			.reclaimPolicy(ReclaimPolicyType.RETAIN)
			.createdAt(LocalDateTime.now())
			.creatorUserName(createStorageClassDTO.getCreatorUserName())
			.creatorId(createStorageClassDTO.getCreatorId())
			.build();
	}

	@Override
	public HasMetadata createResource() {
		return new StorageClassBuilder()
			.withMetadata(createMeta())
			.withParameters(parameters)
			.withProvisioner(provisioner.getProvisionerName())
			.withReclaimPolicy(reclaimPolicy.getField())
			.build();
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getJobName()) //vo-uuid
			.addToAnnotations(createAnnotation())
			.addToLabels(createLabels())
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.STORAGE;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	private HashMap<String, String> createLabels() {
		HashMap<String, String> labels = new HashMap<>();
		labels.put(LabelField.CREATOR_ID.getField(), getCreatorId());
		labels.put(LabelField.STORAGE_TYPE.getField(), storageType.name());
		labels.put(LabelField.CONTROL_BY.getField(), "astra");
		return labels;
	}

	private HashMap<String, String> createAnnotation() {
		HashMap<String, String> annotation = new HashMap<>();
		annotation.put(AnnotationField.NAME.getField(), getName());
		annotation.put(AnnotationField.DESCRIPTION.getField(), getDescription());
		annotation.put(AnnotationField.CREATED_AT.getField(), String.valueOf(getCreatedAt()));
		annotation.put(AnnotationField.CREATOR_USER_NAME.getField(), getCreatorUserName());
		annotation.put(AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorFullName());
		return annotation;
	}
}
