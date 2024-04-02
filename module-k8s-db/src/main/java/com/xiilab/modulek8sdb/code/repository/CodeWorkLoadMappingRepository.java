package com.xiilab.modulek8sdb.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;

public interface CodeWorkLoadMappingRepository extends JpaRepository<CodeWorkLoadMappingEntity, Long>{
	@Modifying(clearAutomatically = true)
	@Query("update CodeWorkLoadMappingEntity dwme "
		+ "set dwme.deleteYN = 'Y'"
		+ "where dwme.workload.id =:jobId")
	void deleteByWorkloadId(Long jobId);
}
