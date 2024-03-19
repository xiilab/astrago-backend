package com.xiilab.serverbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.serverbatch.common.BatchJob;
import com.xiilab.serverbatch.entity.ResourceSchedulerEntity;

public interface ResourceSchedulerRepository extends JpaRepository<ResourceSchedulerEntity, Long> {
	ResourceSchedulerEntity findByJobType(BatchJob jobType);
}
