package com.xiilab.servercore.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.license.entity.LicenseEntity;

@Repository
public interface LicenseRepository extends JpaRepository<LicenseEntity, Long> {
	LicenseEntity findTopByOrderByRegDateDesc();
}
