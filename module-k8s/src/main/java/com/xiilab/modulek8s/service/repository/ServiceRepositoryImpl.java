package com.xiilab.modulek8s.service.repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.service.vo.ServiceVO;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceRepository{

	private final K8sAdapter k8sAdapter;

	@Override
	public void createService(ServiceVO serviceVO) {
		try (KubernetesClient client = new KubernetesClientBuilder().build()) {
			Service service = client.resource(serviceVO.createResource()).create();
			System.out.println("service = " + service);
		}
	}

	@Override
	public void deleteService(String workSpaceName, String workloadName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.services().inNamespace(workloadName).withLabelSelector(workloadName).delete();
		}
	}
}
