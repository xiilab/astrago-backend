package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.workload.history.entity.ExperimentEntity;

@Repository
public interface ExperimentRepo extends JpaRepository<ExperimentEntity, Long> {
	@Query("select t from TB_EXPERIMENT t where t.uuid in ?1")
	List<ExperimentEntity> findByUuidIn(Collection<String> uuids);

	@Query("select t from TB_EXPERIMENT t where t.uuid = ?1")
	Optional<ExperimentEntity> findByUuid(String uuid);
}
