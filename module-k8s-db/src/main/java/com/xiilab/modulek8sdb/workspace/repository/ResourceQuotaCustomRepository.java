package com.xiilab.modulek8sdb.workspace.repository;

import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;

public interface ResourceQuotaCustomRepository {
	ResourceQuotaEntity findByWorkspaceRecently(String name);
}
