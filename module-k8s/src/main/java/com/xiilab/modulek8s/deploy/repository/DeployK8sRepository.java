package com.xiilab.modulek8s.deploy.repository;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public interface DeployK8sRepository {
	List<Pod> getReplicasByDeployResourceName(String workspaceResourceName, String deployResourceName);
}
