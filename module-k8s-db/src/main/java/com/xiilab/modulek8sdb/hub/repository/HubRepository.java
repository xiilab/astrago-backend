package com.xiilab.modulek8sdb.hub.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
@Repository
public interface HubRepository extends JpaRepository<HubEntity, Long> {
	@Query("select t from TB_HUB t where t.workloadType = ?1")
	List<HubEntity> findByWorkloadType(WorkloadType workloadType);
}
