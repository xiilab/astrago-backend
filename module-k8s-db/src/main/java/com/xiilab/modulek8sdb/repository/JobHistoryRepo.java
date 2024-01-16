package com.xiilab.modulek8sdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.xiilab.modulek8sdb.entity.JobEntity;

@Repository
public interface JobHistoryRepo extends JpaRepository<JobEntity, Long> {
}
