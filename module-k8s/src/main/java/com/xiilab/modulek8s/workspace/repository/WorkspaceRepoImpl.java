package com.xiilab.modulek8s.workspace.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceReqVO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceRepoImpl implements WorkspaceRepo {
	private final K8sAdapter k8sAdapter;

	@Override
	public WorkspaceResVO createWorkSpace(WorkspaceReqVO workspaceReqVO) {
		Namespace namespace = (Namespace)createResource(workspaceReqVO.createResource());
		return new WorkspaceResVO(namespace);
	}

	@Override
	public WorkspaceResVO getWorkspaceByName(String name) {
		Namespace namespace;
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			namespace = kubernetesClient.namespaces().withName(name).get();
		}
		return new WorkspaceResVO(namespace);
	}

	@Override
	public List<WorkspaceResVO> getWorkspaceList() {
		List<Namespace> items;
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			items = kubernetesClient.namespaces().list().getItems();
		}
		return items.stream().map(WorkspaceResVO::new).toList();
	}

	@Override
	public void deleteWorkspaceByName(String name) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.namespaces().withName(name).delete();
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

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}
}
