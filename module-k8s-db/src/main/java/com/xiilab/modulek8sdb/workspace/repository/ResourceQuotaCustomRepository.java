package com.xiilab.modulek8sdb.workspace.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;

public interface ResourceQuotaCustomRepository {
	ResourceQuotaEntity findByWorkspaceRecently(String name);

	List<ResourceQuotaEntity> findResourceQuotaByPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
