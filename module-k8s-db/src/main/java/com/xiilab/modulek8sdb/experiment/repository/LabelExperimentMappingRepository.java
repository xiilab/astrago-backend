package com.xiilab.modulek8sdb.experiment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.workload.history.entity.LabelExperimentMappingEntity;

@Repository
public interface LabelExperimentMappingRepository extends JpaRepository<LabelExperimentMappingEntity, Long> {
}
