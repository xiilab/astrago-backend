package com.xiilab.modulek8sdb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;

public interface ModelWorkLoadMappingRepository extends JpaRepository<ModelWorkLoadMappingEntity, Long> {
	@Modifying(clearAutomatically = true)
	@Query("update ModelWorkLoadMappingEntity dwme "
		+ "set dwme.deleteYN = 'Y'"
		+ "where dwme.workload.id =:jobId")
	void deleteByWorkloadId(@Param("jobId") Long jobId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update ModelWorkLoadMappingEntity mwm "
		+ "set mwm.deleteYN = 'Y' "
		+ "where mwm.model.modelId = :modelId")
	void deleteByModelId(@Param("modelId") Long modelId);
}
