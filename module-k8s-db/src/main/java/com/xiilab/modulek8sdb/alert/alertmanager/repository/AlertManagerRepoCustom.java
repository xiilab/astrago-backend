package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerReceiveEntity;

public interface AlertManagerRepoCustom {
	Page<AlertManagerReceiveEntity> getAlertManagerReceiveList(String categoryType, String search, LocalDateTime start, LocalDateTime end, String userId, Pageable pageable);
}
