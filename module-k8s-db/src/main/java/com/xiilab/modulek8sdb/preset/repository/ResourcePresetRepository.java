package com.xiilab.modulek8sdb.preset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.preset.entity.ResourcePresetEntity;

public interface ResourcePresetRepository extends JpaRepository<ResourcePresetEntity, Long>, ResourcePresetRepositoryCustom {
}
