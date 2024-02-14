package com.xiilab.modulek8sdb.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {
}
