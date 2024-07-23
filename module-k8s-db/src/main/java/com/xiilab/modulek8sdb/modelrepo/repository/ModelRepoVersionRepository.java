package com.xiilab.modulek8sdb.modelrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;

@Repository
public interface ModelRepoVersionRepository extends JpaRepository<ModelVersionEntity, Long> {
}
