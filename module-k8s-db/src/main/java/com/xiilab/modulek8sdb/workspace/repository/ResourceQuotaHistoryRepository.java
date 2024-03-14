package com.xiilab.modulek8sdb.workspace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;

@Repository
public interface ResourceQuotaHistoryRepository extends JpaRepository<ResourceQuotaEntity, Long> {
	List<ResourceQuotaEntity> findByWorkspaceResourceName(String workspace);

	@Transactional
	@Modifying
	@Query("delete from TB_RESOURCE_QUOTA t where t.workspaceResourceName = ?1")
	int deleteByWorkspaceResourceName(String workspaceResourceName);
}
