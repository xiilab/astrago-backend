package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;

public interface SystemAlertRepositoryCustom {
	Page<SystemAlertEntity> findAlerts(String recipientId, AlertType alertType, AlertRole alertRole, ReadYN readYN, String searchText,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate, Pageable pageable);
}
