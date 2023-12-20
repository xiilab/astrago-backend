package com.xiilab.modulek8s.resource_quota.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaReqVO;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaResVO;

import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResourceQuotaRepoImpl implements ResourceQuotaRepo {
	private final K8sAdapter k8sAdapter;

	@Override
	public void createResourceQuotas(ResourceQuotaReqVO resourceQuotaReqVO) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		kubernetesClient.resource(resourceQuotaReqVO.createResource()).create();
	}

	@Override
	public void deleteResourceQuotas(String name, String namespace) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		kubernetesClient.resourceQuotas().inNamespace(namespace).withName(name).delete();
	}

	@Override
	public ResourceQuotaResVO getResourceQuotas(String name, String namespace) {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		ResourceQuota resourceQuota = kubernetesClient.resourceQuotas().inNamespace(namespace).withName(name).get();
		return new ResourceQuotaResVO(resourceQuota);
	}
}
