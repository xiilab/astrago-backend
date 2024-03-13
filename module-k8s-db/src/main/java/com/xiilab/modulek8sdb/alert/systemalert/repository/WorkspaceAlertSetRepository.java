package com.xiilab.modulek8sdb.alert.systemalert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertSetEntity;

public interface WorkspaceAlertSetRepository extends JpaRepository<WorkspaceAlertSetEntity, Long> {
	WorkspaceAlertSetEntity getAlertSetEntityByWorkspaceName(String workspaceName);
}
