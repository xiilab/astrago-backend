package com.xiilab.modulek8sdb.workload.history.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;

@Repository
public interface WorkloadHistoryRepo extends JpaRepository<JobEntity, Long> {
	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1 and t.resourceName = ?2")
	Optional<JobEntity> findByWorkspaceResourceNameAndResourceName(String workspaceResourceName, String resourceName);
	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1 and t.creatorId = ?2")
	List<JobEntity> findByWorkspaceResourceNameAndCreatorId(String workspaceResourceName, String creatorId);

	@Query("select t from TB_WORKLOAD_JOB t where t.workspaceResourceName = ?1")
	List<JobEntity> findByWorkspaceResourceName(String workspaceResourceName);
}
