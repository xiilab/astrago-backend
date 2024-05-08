package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;

@Repository
public interface WorkloadHistoryRepo extends JpaRepository<JobEntity, Long> {
	@Query("select t from TB_WORKLOAD_JOB t where t.resourceName = ?1")
	Optional<JobEntity> findByResourceName(String resourceName);

	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1 and t.resourceName = ?2")
	Optional<JobEntity> findByWorkspaceResourceNameAndResourceName(String workspaceResourceName, String resourceName);

	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1 and t.creatorId = ?2")
	List<JobEntity> findByWorkspaceResourceNameAndCreatorId(String workspaceResourceName, String creatorId);

	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1")
	List<JobEntity> findByWorkspaceResourceName(String workspaceResourceName);

	@Transactional
	@Modifying
	@Query("update TB_WORKLOAD_JOB t set t.workloadStatus = ?1 where t.resourceName = ?2")
	void updateWorkloadStatusByResourceName(@NonNull WorkloadStatus workloadStatus, String resourceName);
}
