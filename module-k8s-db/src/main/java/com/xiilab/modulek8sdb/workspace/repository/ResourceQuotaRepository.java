package com.xiilab.modulek8sdb.workspace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;

@Repository
public interface ResourceQuotaRepository extends JpaRepository<ResourceQuotaEntity, Long> {
	List<ResourceQuotaEntity> findByWorkspaceResourceName(String workspace);
}
