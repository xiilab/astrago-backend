package com.xiilab.modulek8s.storage.volume.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeRepository;
import com.xiilab.modulek8s.storage.volume.vo.VolumeVO;
import com.xiilab.modulemonitor.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VolumeRepositoryImpl implements VolumeRepository {
	private final K8sAdapter k8sAdapter;
	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO) {
		VolumeVO volumeVO = VolumeVO.dtoToVo(createVolumeDTO);
		try(final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaim resource = (PersistentVolumeClaim)volumeVO.createResource();
			client.persistentVolumeClaims().resource(resource).create();
		}
	}
}
