package com.xiilab.modulek8sdb.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;

public interface ImageWorkloadMappingRepository extends JpaRepository<ImageWorkloadMappingEntity, Long> {
	@Modifying(clearAutomatically = true)
	@Query("update ImageWorkloadMappingEntity dwme "
		+ "set dwme.deleteYN = 'Y'"
		+ "where dwme.workload.id =:jobId")
	void deleteByWorkloadId(Long jobId);
}
