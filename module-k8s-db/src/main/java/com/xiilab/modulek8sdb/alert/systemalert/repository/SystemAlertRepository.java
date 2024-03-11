package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;

public interface SystemAlertRepository extends JpaRepository<SystemAlertEntity, Long> {
	List<SystemAlertEntity> getAlertEntitiesByRecipientId(String recipientId);
}
