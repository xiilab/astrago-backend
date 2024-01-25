package com.xiilab.servercore.storage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.servercore.storage.entity.StorageEntity;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {
}
