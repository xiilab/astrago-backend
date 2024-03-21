package com.xiilab.modulek8s.workload.svc.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.workload.svc.vo.ClusterIPSvcVO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.svc.vo.NodeSvcVO;

import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class SvcRepositoryImpl implements SvcRepository {
	private final K8sAdapter k8sAdapter;
	@Override
	public void createNodePortService(NodeSvcVO nodeSvcVO) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.resource(nodeSvcVO.createResource()).create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteService(String workSpaceName, String workloadName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.services().inNamespace(workloadName).withLabelSelector(workloadName).delete();
		}
	}

	@Override
	public void createClusterIPService(ClusterIPSvcVO serviceDtoToServiceVO) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.resource(serviceDtoToServiceVO.createResource()).create();
		}
	}

	@Override
	public void deleteServiceByResourceName(String svcName, String namespace) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.services().inNamespace(namespace).withName(svcName).delete();
		}
	}

	@Override
	public ServiceList getServicesByResourceName(String workspaceResourceName, String workloadResourcedName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			return client.services()
				.inNamespace(workspaceResourceName)
				.withLabel(LabelField.WORKLOAD_RESOURCE_NAME.getField(), workloadResourcedName)
				.list();
		}
	}
}
