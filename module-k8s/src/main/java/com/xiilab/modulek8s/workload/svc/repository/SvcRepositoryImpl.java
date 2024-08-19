package com.xiilab.modulek8s.workload.svc.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.workload.svc.vo.ClusterIPSvcVO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.svc.vo.NodeSvcVO;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
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
		}
	}

	@Override
	public void deleteService(String workSpaceName, String workloadName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.services().inNamespace(workSpaceName).withLabelSelector(workloadName).delete();
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
	@Override
	public List<List<ServicePort>> getPortsByWorkloadResourceName(String namespace, String resourceName) {
		List<List<ServicePort>> ports = new ArrayList<>();
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Service> items = client.services()
				.inNamespace(namespace)
				.withLabel(LabelField.WORKLOAD_RESOURCE_NAME.getField(), resourceName)
				.list()
				.getItems();
			for (Service item : items) {
				ports.add(item.getSpec().getPorts());
			}
		}
		return ports;
	}
}
