package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;

public interface AlertRepositoryCustom {
	List<AlertEntity> getWorkspaceAlertsByOwnerRole();
	List<AlertEntity> findAdminAlertMappings(String adminId);
}
