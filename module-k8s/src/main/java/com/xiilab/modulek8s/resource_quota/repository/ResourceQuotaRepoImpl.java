package com.xiilab.modulek8s.resource_quota.repository;

import static com.xiilab.modulek8s.resource_quota.enumeration.ResourceQuotaKey.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaReqVO;
import com.xiilab.modulek8s.resource_quota.vo.ResourceQuotaResVO;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuota;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResourceQuotaRepoImpl implements ResourceQuotaRepo {
	private final K8sAdapter k8sAdapter;

	@Override
	public void createResourceQuotas(ResourceQuotaReqVO resourceQuotaReqVO) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resource(resourceQuotaReqVO.createResource()).create();
		}
	}

	@Override
	public void deleteResourceQuotas(String name, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.resourceQuotas().inNamespace(namespace).withName(name).delete();
		}
	}

	@Override
	public ResourceQuotaResVO getResourceQuotas(String namespace) {
		ResourceQuota resourceQuota;
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			resourceQuota = kubernetesClient.resourceQuotas().inNamespace(namespace).list().getItems().get(0);
			return new ResourceQuotaResVO(resourceQuota);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void updateResourceQuota(String workspace, int cpuReq, int memReq, int gpuReq) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			ResourceQuota resourceQuota = kubernetesClient
				.resourceQuotas()
				.inNamespace(workspace)
				.list()
				.getItems()
				.get(0);

			resourceQuota.setSpec(resourceQuota.getSpec().edit().addToHard(Map.of(
				REQUEST_CPU_KEY.getKey(), new Quantity(String.valueOf(cpuReq)),
				REQUEST_MEMORY_KEY.getKey(), new Quantity(String.valueOf(memReq), "Gi"),
				REQUEST_GPU_KEY.getKey(), new Quantity(String.valueOf(gpuReq)),
				LIMITS_CPU_KEY.getKey(), new Quantity(String.valueOf(cpuReq)),
				LIMITS_MEMORY_KEY.getKey(), new Quantity(String.valueOf(memReq), "Gi"),
				LIMITS_GPU_KEY.getKey(), new Quantity(String.valueOf(gpuReq))
			)).build());
			kubernetesClient.resource(resourceQuota).replace();
		}
	}

	@Override
	public List<ResourceQuotaResVO> getResourceQuotasList() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resourceQuotas()
				.inAnyNamespace()
				.list()
				.getItems()
				.stream()
				.map(ResourceQuotaResVO::new)
				.toList();
		}
	}
}
