package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerReceiveEntity;

public interface AlertManagerRepoCustom {
	List<AlertManagerReceiveEntity> getAlertManagerReceiveList(String categoryType, String search, LocalDateTime start, LocalDateTime end, String userId);
}
