package com.xiilab.modulek8s.storage.volume.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.enums.AccessMode;
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
	public void createVolume(CreateVolumeDTO createVolumeDTO) {
		String workspaceMetaDataName = createVolumeDTO.getWorkspaceMetaDataName();
		String volumeName = createVolumeDTO.getVolumeName();
		int requestVolume = createVolumeDTO.getRequestVolume();
		String provisioner = createVolumeDTO.getStorageMetaName();
		try(final KubernetesClient client = k8sAdapter.configServer()){
		//1. ns에 pvc, pv 생성
			PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
				.withNewMetadata()
				.withName("testtest") //vo-uuid
				.withNamespace(workspaceMetaDataName)
				.addToLabels("volume-name", volumeName)
				.endMetadata()
				.withNewSpec()
				.withStorageClassName(provisioner) //st-uuid
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
