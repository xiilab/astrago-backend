package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerEntity;

@Repository
public interface AlertManagerRepository extends JpaRepository<AlertManagerEntity, Long> {
}