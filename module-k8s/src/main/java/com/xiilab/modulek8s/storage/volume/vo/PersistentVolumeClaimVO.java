package com.xiilab.modulek8s.storage.volume.vo;

import com.xiilab.modulek8s.common.enumeration.AccessMode;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;

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
public class PersistentVolumeClaimVO extends K8SResourceReqVO {
	private String pvcMetaName;
	private String namespace;
	private int requestVolume;
	private String storageClassName;

	@Override
	public HasMetadata createResource() {
		return new PersistentVolumeClaimBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	@Override
	public HasMetadata createResource(String userUUID) {
		throw new UnsupportedOperationException("Unimplemented method 'createResource'");
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(pvcMetaName) //vo-uuid
			.withNamespace(namespace)
			.build();
	}
	private PersistentVolumeClaimSpec createSpec() {
		return new PersistentVolumeClaimSpecBuilder()
			.withAccessModes(AccessMode.RWM.getAccessMode())
			.withNewResources()
			.addToRequests("storage", new Quantity(requestVolume + "Gi"))
			.endResources()
			.build();
	}

	public static PersistentVolumeClaimVO dtoToEntity(CreatePVC createPVC){
		return PersistentVolumeClaimVO.builder()
			.pvcMetaName(createPVC.getPvcName())
			.namespace(createPVC.getNamespace())
			.requestVolume(createPVC.getRequestVolume())
			.storageClassName(createPVC.getStorageClassName())
			.build();
	}
	@Override
	protected ResourceType getType() {
		return ResourceType.PVC;
	}

	
}
