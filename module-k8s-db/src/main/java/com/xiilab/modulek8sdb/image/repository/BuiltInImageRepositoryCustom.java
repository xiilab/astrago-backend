package com.xiilab.modulek8sdb.image.repository;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;

public interface BuiltInImageRepositoryCustom {
	List<BuiltInImageEntity> findByType(WorkloadType type);
}
