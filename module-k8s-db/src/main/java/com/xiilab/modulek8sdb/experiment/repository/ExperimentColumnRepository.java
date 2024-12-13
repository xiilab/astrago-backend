package com.xiilab.modulek8sdb.experiment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.experiment.entity.ExperimentColumnEntity;

@Repository
public interface ExperimentColumnRepository extends JpaRepository<ExperimentColumnEntity, Long> {
	@Query("select e from ExperimentColumnEntity e where e.workspace = ?1 and e.regUser.regUserId = ?2")
	List<ExperimentColumnEntity> findByWorkspaceAndRegUser_RegUserId(String workspace, String regUserId);
}
