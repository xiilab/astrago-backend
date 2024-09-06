package com.xiilab.modulek8sdb.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;

public interface CodeWorkLoadMappingRepository extends JpaRepository<CodeWorkLoadMappingEntity, Long>{
	@Modifying(clearAutomatically = true)
	@Query("update CodeWorkLoadMappingEntity dwme "
		+ "set dwme.deleteYN = 'Y'"
		+ "where dwme.workload.id =:jobId")
	void deleteByWorkloadId(@Param("jobId") Long jobId);

	@Modifying(clearAutomatically = true)
	@Query("""
		delete 
		from CodeWorkLoadMappingEntity dwme
		where dwme.code.id =:codeId
""")
	void deleteAllByCodeId(@Param("codeId") Long id);
}
