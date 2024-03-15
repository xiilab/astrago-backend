package com.xiilab.modulek8sdb.alert.systemalert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;

public interface SystemAlertRepositoryCustom {
	Page<SystemAlertEntity> findAlerts(String recipientId, Pageable pageable);
}
