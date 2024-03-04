package com.xiilab.modulek8sdb.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;

public interface ImageWorkloadMappingRepository extends JpaRepository<ImageWorkloadMappingEntity, Long> {
}
