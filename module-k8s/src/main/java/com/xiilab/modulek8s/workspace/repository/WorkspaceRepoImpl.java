package com.xiilab.modulek8s.workspace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepoImpl implements WorkspaceRepo {
	private final K8sAdapter k8sAdapter;

	@Override
	public WorkspaceVO.ResponseVO createWorkSpace(WorkspaceVO.RequestVO workspaceReqVO) {
		Namespace namespace = (Namespace)createResource(workspaceReqVO.createResource());
		return new WorkspaceVO.ResponseVO(namespace);
	}

	@Override
	public WorkspaceVO.ResponseVO getWorkspaceByName(String name) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Namespace namespace = kubernetesClient.namespaces().withName(name).get();
			if (namespace == null) {
				throw new K8sException(WorkspaceErrorCode.NOT_FOUND_WORKSPACE);
			}
			return new WorkspaceVO.ResponseVO(namespace);
		}
	}

	@Override
	public List<WorkspaceVO.ResponseVO> getWorkspaceList() {
		List<Namespace> items;
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			items = kubernetesClient.namespaces().withLabels(Map.of("control-by","astra")).list().getItems();
		}
		return items.stream().map(WorkspaceVO.ResponseVO::new).toList();
	}

	@Override
	public void deleteWorkspaceByName(String name) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.namespaces().withName(name).withGracePeriod(0).delete();
		}
	}

	@Override
	public void updateWorkspaceInfo(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.namespaces().withName(workspaceName).edit(ns
				-> ns.edit()
				.editMetadata()
				.addToAnnotations(Map.of(
					AnnotationField.NAME.getField(), updateDTO.getName(),
					AnnotationField.DESCRIPTION.getField(), updateDTO.getDescription()))
				.endMetadata()
				.build());
		} catch (KubernetesClientException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	@Override
	public WorkspaceDTO.WorkspaceResourceStatus getWorkspaceResourceStatus(String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//namespace 조회
			WorkspaceVO.ResponseVO workspaceByName = getWorkspaceByName(namespace);
			// namespace의 resourceQuota 조회
			ResourceQuotaStatus resourceQuota = kubernetesClient.resourceQuotas()
				.inNamespace(namespace)
				.list()
				.getItems()
				.get(0)
				.getStatus();
			return new WorkspaceDTO.WorkspaceResourceStatus(workspaceByName, resourceQuota);
		}
	}

	@Override
	public String getNodeName(String workspaceResourceName, String workloadResourceName){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Pod pod = kubernetesClient.pods()
				.inNamespace(workspaceResourceName)
				.list()
				.getItems()
				.stream()
				.filter(pod1 ->
					pod1.getMetadata().getName().contains(workloadResourceName))
				.findFirst()
				.orElse(null);
			return pod.getSpec().getNodeName();
		}
	}

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}

}
