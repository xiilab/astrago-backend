package com.xiilab.modulek8sdb.image.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;

@Repository
public interface BuiltInImageRepository extends JpaRepository<BuiltInImageEntity, Long>, BuiltInImageRepositoryCustom {
}
