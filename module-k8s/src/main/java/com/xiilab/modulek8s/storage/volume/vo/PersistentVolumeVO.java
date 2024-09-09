package com.xiilab.modulek8s.storage.volume.vo;

import java.util.Collections;

import com.xiilab.modulek8s.common.enumeration.AccessMode;
import com.xiilab.modulek8s.common.enumeration.ReclaimPolicyType;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PersistentVolumeVO extends K8SResourceReqVO {
	private StorageType storageType;
	private String pvMetaName;
	private String pvcMetaName;
	private String ip;
	private String storagePath;
	private String namespace;
	private int requestVolume;
	private String arrayId;
	private String dellVolumeId;

	@Override
	public HasMetadata createResource() {
		return new PersistentVolumeBuilder()
			.withMetadata(createMeta())
			.withNewMetadata().withName(pvMetaName).endMetadata()
			.withSpec(createSpec())
			.build();
	}

	private ObjectReference createObjectReference() {
		ObjectReference objectReference = new ObjectReference().toBuilder().build();
		objectReference.setKind("PersistentVolumeClaim");
		objectReference.setApiVersion("v1");
		objectReference.setNamespace(namespace);
		objectReference.setName(pvcMetaName);
		return objectReference;
	}
	public static PersistentVolumeVO dtoToEntity(CreatePV createPV){
		return PersistentVolumeVO.builder()
			.pvMetaName(createPV.getPvName())
			.pvcMetaName(createPV.getPvcName())
			.ip(createPV.getIp())
			.namespace(createPV.getNamespace())
			.storagePath(createPV.getStoragePath())
			.storageType(createPV.getStorageType())
			.requestVolume(createPV.getRequestVolume())
			.arrayId(createPV.getArrayId())
			.dellVolumeId(createPV.getDellVolumeId())
			.build();
	}
	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(pvMetaName)
			.withNamespace(namespace)
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.PV;
	}
	private PersistentVolumeSpec createSpec() {
		if(storageType.name().equals(StorageType.NFS.name())){
			return new PersistentVolumeSpecBuilder()
				.addToCapacity(Collections.singletonMap("storage", new Quantity(requestVolume + "Gi")))
				.withAccessModes(AccessMode.RWM.getAccessMode())
				.withPersistentVolumeReclaimPolicy(ReclaimPolicyType.RETAIN.getField())
				.withNewNfs()
				.withServer(ip)
				.withPath(storagePath)
				.endNfs()
				.withClaimRef(createObjectReference())
				.build();
		}
		return null;
	}
}
