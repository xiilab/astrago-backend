package com.xiilab.servercore.workspace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.workspace.entity.ResourceQuotaEntity;

@Repository
public interface ResourceQuotaRepository extends JpaRepository<ResourceQuotaEntity, Long> {
	List<ResourceQuotaEntity> findByWorkspace(String workspace);
}
