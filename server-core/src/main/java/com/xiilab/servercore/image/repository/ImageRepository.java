package com.xiilab.servercore.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.image.entity.ImageEntity;
@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
