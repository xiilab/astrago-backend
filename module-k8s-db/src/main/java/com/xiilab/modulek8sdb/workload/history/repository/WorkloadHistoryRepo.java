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

import com.xiilab.modulecommon.enums.GPUType;
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

	@Transactional
	@Modifying
	@Query("update TB_WORKLOAD t set t.endTime = :now where t.resourceName = :resourceName")
	void updateWorkloadEndTime(@Param("resourceName") String resourceName, @Param("now") LocalDateTime now);

	@Query("""
  			select t
  			from TB_WORKLOAD t
  			join TB_WORKLOAD_JOB twj on t.id = twj.id 
  			where t.workspaceResourceName = :workspaceResourceName 
  				and t.workloadStatus in (:statuses)
  				and twj.gpuType in (:types)
		""")
	List<WorkloadEntity> getWorkloadHistoryByUsingDivisionGPU(@Param("workspaceResourceName") String workspaceResourceName, @Param("statuses") List<WorkloadStatus> statuses, @Param("types") List<GPUType> types);

	@Transactional
	@Modifying
	@Query("""
		update TB_WORKLOAD t 
		set t.gpuOnePerMemory = :memory,
		t.gpuName = CASE WHEN :gpuName IS NULL THEN t.gpuName ELSE :gpuName END 
		where t.resourceName = :resourceName
""")
	void insertGpuInfo(@Param("resourceName") String resourceName, @Param("gpuName") String gpuName, @Param("memory") int memory);

	@Transactional
	@Modifying
	@Query("update TB_WORKLOAD t set t.workspaceName = ?1 where t.workspaceResourceName = ?2")
	void updateWorkspaceNameByWorkspaceResourceName(String workspaceName, String workspaceResourceName);
}
