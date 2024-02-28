package com.xiilab.modulealert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.AlertManagerEntity;

@Repository
public interface AlertManagerRepository  extends JpaRepository<AlertManagerEntity, Long> {
}
