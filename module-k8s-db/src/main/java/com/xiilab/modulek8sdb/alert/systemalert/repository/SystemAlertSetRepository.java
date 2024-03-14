package com.xiilab.modulek8sdb.alert.systemalert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;

public interface SystemAlertSetRepository extends JpaRepository<SystemAlertSetEntity, Long> {
}
