package com.xiilab.modulek8sdb.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.report.entity.ResourceOptimizationJobEntity;

@Repository
public interface ResourceOptimizationJobReopsitory extends JpaRepository<ResourceOptimizationJobEntity, Long> {
}