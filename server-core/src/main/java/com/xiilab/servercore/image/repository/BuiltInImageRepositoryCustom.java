package com.xiilab.servercore.image.repository;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.image.entity.BuiltInImageEntity;

public interface BuiltInImageRepositoryCustom {
	List<BuiltInImageEntity> findByType(WorkloadType type);
}
