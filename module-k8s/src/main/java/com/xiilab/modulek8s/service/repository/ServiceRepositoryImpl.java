package com.xiilab.modulek8s.service.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.service.vo.ServiceVO;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class ServiceRepositoryImpl implements ServiceRepository{

	@Override
	public void createService(ServiceVO serviceVO) {
		try (KubernetesClient client = new KubernetesClientBuilder().build()) {
			Service service = client.resource(serviceVO.createResource()).create();
			System.out.println("service = " + service);
		}
	}
}
