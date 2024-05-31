package com.xiilab.modulek8sdb.workload.history.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

@Repository
public interface WorkloadHistoryRepo extends JpaRepository<WorkloadEntity, Long> {
	@Query("select t from TB_WORKLOAD t where t.resourceName = ?1")
	Optional<WorkloadEntity> findByResourceName(String resourceName);

	@Query("select t from TB_WORKLOAD t where t.workspaceResourceName = ?1 and t.resourceName = ?2")
	Optional<WorkloadEntity> findByWorkspaceResourceNameAndResourceName(String workspaceResourceName,
		String resourceName);

	@Query("select t from TB_WORKLOAD t where t.workspaceResourceName = ?1")
	List<WorkloadEntity> findByWorkspaceResourceName(String workspaceResourceName);

	@Transactional
	@Modifying
	@Query("update TB_WORKLOAD t set t.workloadStatus = ?1 where t.resourceName = ?2")
	void updateWorkloadStatusByResourceName(@NonNull WorkloadStatus workloadStatus, String resourceName);

	@Query("select t from TB_WORKLOAD t where t.workspaceResourceName = :workspaceResourceName and t.workloadStatus = :workloadStatus")
	List<WorkloadEntity> getWorkloadByResourceNameAndStatus(
		@Param("workspaceResourceName") String workspaceResourceName,
		@Param("workloadStatus") WorkloadStatus workloadStatus);

	@Transactional
	@Modifying
	@Query("update TB_WORKLOAD t set t.startTime = :now where t.resourceName = :resourceName")
	void insertWorkloadStartTime(@Param("resourceName") String resourceName, @Param("now") LocalDateTime now);
}
