package com.xiilab.modulek8sdb.workload.history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;

@Repository
public interface PortRepository extends JpaRepository<PortEntity, Long> {
}