package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;

public interface WorkspaceAlertMappingRepositoryCustom {
	List<WorkspaceAlertMappingEntity> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String workspaceResourceName, String userId, AlertRole alertRole);

	List<WorkspaceAlertMappingEntity> getWorkspaceAlertMappingByAlertId(Long alertId, String workspaceResourceName);
}
