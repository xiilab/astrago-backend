package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;

public interface SystemAlertRepositoryCustom {
	Page<SystemAlertEntity> findAlerts(String recipientId, SystemAlertType systemAlertType, ReadYN readYN,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate, Pageable pageable);
}
