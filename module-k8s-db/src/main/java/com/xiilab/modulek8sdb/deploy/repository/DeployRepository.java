package com.xiilab.modulek8sdb.deploy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;

public interface DeployRepository extends JpaRepository<DeployEntity, Long> {

	@Query("select de from DeployEntity de where de.resourceName = :resourceName")
	Optional<Object> findByResourceName(@Param("resourceName") String resourceName);
}
