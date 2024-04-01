package com.xiilab.modulek8sdb.dataset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;

public interface DatasetWorkLoadMappingRepository extends JpaRepository<DatasetWorkLoadMappingEntity, Long>{
	@Modifying(clearAutomatically = true)
	@Query("update DatasetWorkLoadMappingEntity dwme "
		+ "set dwme.deleteYN = 'Y'"
		+ "where dwme.workload.id =:jobId")
	void deleteByWorkloadId(@Param("jobId") Long jobId);


}
