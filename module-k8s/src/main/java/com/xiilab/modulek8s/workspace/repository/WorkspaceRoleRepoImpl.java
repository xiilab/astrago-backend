package com.xiilab.modulek8s.workspace.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.rbac.PolicyRule;
import io.fabric8.kubernetes.api.model.rbac.Role;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.fabric8.kubernetes.api.model.rbac.RoleBindingBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleBuilder;
import io.fabric8.kubernetes.api.model.rbac.RoleRef;
import io.fabric8.kubernetes.api.model.rbac.Subject;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceRoleRepoImpl implements WorkspaceRoleRepo {
	private final K8sAdapter k8sAdapter;

	@Override
	public void editWorkspaceRole(String workspaceResourceName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<PolicyRule> policyRuleList = new ArrayList<>();
			PolicyRule endpoints = new PolicyRule();
			endpoints.setApiGroups(List.of(""));
			endpoints.setResources(List.of("pods"));
			endpoints.setVerbs(Arrays.asList("patch", "get"));
			policyRuleList.add(endpoints);

			Role createdRole = new RoleBuilder()
				.withNewMetadata()
				.withName("edit-pod-annotations")
				.withNamespace(workspaceResourceName)
				.endMetadata()
				.addAllToRules(policyRuleList)
				.build();

			client.rbac().roles().resource(createdRole).create();
		}
	}

	@Override
	public void createPodAnnotationsRoleBinding(String workspaceResourceName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Subject> subjects = new ArrayList<>();
			Subject subject = new Subject();
			subject.setNamespace(workspaceResourceName);
			subject.setKind("ServiceAccount");
			subject.setName("default");
			subjects.add(subject);


			RoleRef roleRef = new RoleRef();
			roleRef.setName("edit-pod-annotations");
			roleRef.setKind("Role");
			roleRef.setApiGroup("rbac.authorization.k8s.io");

			RoleBinding roleBindingToCreate = new RoleBindingBuilder()
				.withNewMetadata().withName("edit-pod-annotations-binding").withNamespace(workspaceResourceName).endMetadata()
				.addAllToSubjects(subjects)
				.withRoleRef(roleRef)
				.build();

			client.rbac().roleBindings().resource(roleBindingToCreate).create();
		}
	}
}
