package com.xiilab.modulek8sdb.alert.systemalert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;

public interface WorkspaceAlertMappingRepository extends JpaRepository<WorkspaceAlertMappingEntity, Long>, WorkspaceAlertMappingRepositoryCustom {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from WorkspaceAlertMappingEntity wame "
		+ "where wame.userId = :userId "
		+ "and wame.workspaceResourceName = :workspaceResourceName")
	void deleteWorkspaceAlertMappingByUserIdAndWorkspaceName(@Param("userId") String userId,@Param("workspaceResourceName")  String workspaceResourceName);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from WorkspaceAlertMappingEntity wame "
		+ "where wame.workspaceResourceName = :workspaceResourceName")
	void deleteWorkspaceAlertMappingByWorkspaceName(@Param("workspaceResourceName") String workspaceResourceName);
}
