package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;

public interface WorkspaceAlertMappingRepository extends JpaRepository<WorkspaceAlertMappingEntity, Long>, WorkspaceAlertMappingRepositoryCustom {
	@Query("""
		select w from WorkspaceAlertMappingEntity w
		where w.alert.alertId = ?1 and w.userId = ?2 and w.workspaceResourceName = ?3""")
	Optional<WorkspaceAlertMappingEntity> findByAlert_AlertIdAndUserIdAndWorkspaceResourceName(Long alertId,
		String userId, String workspaceResourceName);

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
