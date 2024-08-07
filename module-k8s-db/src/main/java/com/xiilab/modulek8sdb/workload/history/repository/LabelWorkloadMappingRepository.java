package com.xiilab.modulek8sdb.workload.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.workload.history.entity.LabelWorkloadMappingEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

@Repository
public interface LabelWorkloadMappingRepository extends JpaRepository<LabelWorkloadMappingEntity, Long> {
	@Transactional
	@Modifying
	@Query("delete from LabelWorkloadMappingEntity l where l.workload = ?1")
	void deleteByWorkloadId(WorkloadEntity workload);
}
