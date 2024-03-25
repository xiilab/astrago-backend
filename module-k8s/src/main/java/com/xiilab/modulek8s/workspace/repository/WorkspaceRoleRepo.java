package com.xiilab.modulek8s.workspace.repository;

public interface WorkspaceRoleRepo {
	void editWorkspaceRole(String workspaceResourceName);
	void createPodAnnotationsRoleBinding(String workspaceResourceName);
}
