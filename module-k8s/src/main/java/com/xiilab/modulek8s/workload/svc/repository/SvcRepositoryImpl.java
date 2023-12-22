package com.xiilab.modulek8s.workload.svc.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.svc.vo.ServiceVO;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SvcRepositoryImpl implements SvcRepository {

	@Override
	public void createNodePortService(ServiceVO serviceVO) {
		try (KubernetesClient client = new KubernetesClientBuilder().build()) {
			client.resource(serviceVO.createResource()).create();
		}
	}
}
