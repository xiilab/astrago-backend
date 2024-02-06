package com.xiilab.servercore.image.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.image.entity.BuiltInImageEntity;

@Repository
public interface BuiltInImageRepository extends JpaRepository<BuiltInImageEntity, Long> {
	@Query("select t from TB_BUILT_IN_IMAGE t where t.type = ?1")
	List<BuiltInImageEntity> findByType(WorkloadType type);
}
