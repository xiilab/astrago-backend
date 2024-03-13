package com.xiilab.modulek8sdb.version.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.version.entity.CompatibleFrameworkVersionEntity;

public interface CompatibleFrameWorkVersionRepository extends JpaRepository<CompatibleFrameworkVersionEntity, Long> {
}
