package com.xiilab.modulek8s.storage.provisioner.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerRepository;
import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.storage.CSIDriver;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProvisionerRepositoryImpl implements ProvisionerRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public List<CSIDriver> findProvisioner() {
		try(final KubernetesClient client = k8sAdapter.configServer()){
			return client.storage().v1().csiDrivers().list().getItems();
		}
	}
}
