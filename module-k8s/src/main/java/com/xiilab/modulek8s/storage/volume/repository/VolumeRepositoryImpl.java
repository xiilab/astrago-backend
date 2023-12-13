package com.xiilab.modulek8s.storage.volume.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.volume.dto.RequestInsertDTO;
import com.xiilab.modulek8s.storage.volume.enums.AccessMode;
import com.xiilab.modulek8s.storage.volume.enums.StorageType;
import com.xiilab.modulek8s.storage.volume.service.VolumeRepository;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VolumeRepositoryImpl implements VolumeRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public void createVolume(RequestInsertDTO requestInsertDTO) {
		String workspaceMetaDataName = requestInsertDTO.getWorkspaceMetaDataName();
		String volumeName = requestInsertDTO.getVolumeName();
		StorageType storageType = requestInsertDTO.getStorageType();
		int requestVolume = requestInsertDTO.getRequestVolume();
		//1. workspace name으로 ns 조회
		try(final KubernetesClient client = k8sAdapter.configServer()){

			//스토리지 타입으로 sc 조회 - nfs.csi.k8s.io

		//2. ns에 pvc, pv 생성
			PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
				.withNewMetadata()
				.withName("testpvc1")
				.withNamespace(workspaceMetaDataName)
				.addToLabels("volume-name", volumeName)
				.endMetadata()
				.withNewSpec()
				.withStorageClassName("nfs-csi")
				.withAccessModes(AccessMode.RWM.getAccessMode())
				.withNewResources()
				.addToRequests("storage", new Quantity(requestVolume + "Gi"))
				.endResources()
				.endSpec()
				.build();

			client.persistentVolumeClaims().resource(persistentVolumeClaim).create();
		}


	}
}
