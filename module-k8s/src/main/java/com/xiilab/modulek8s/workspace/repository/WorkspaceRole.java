package com.xiilab.modulek8s.workspace.repository;

public interface WorkspaceRole {
	void editWorkspaceRole(String workspaceResourceName);
	void createPodAnnotationsRoleBinding(String workspaceResourceName);
}
