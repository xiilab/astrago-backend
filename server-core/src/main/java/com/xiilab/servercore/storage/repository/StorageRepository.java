package com.xiilab.servercore.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.servercore.storage.entity.StorageEntity;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {

}
