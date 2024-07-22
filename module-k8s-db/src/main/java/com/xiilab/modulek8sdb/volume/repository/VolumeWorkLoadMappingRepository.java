package com.xiilab.modulek8sdb.volume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.volume.entity.VolumeWorkLoadMappingEntity;

public interface VolumeWorkLoadMappingRepository extends JpaRepository<VolumeWorkLoadMappingEntity, Long> {
	@Modifying(clearAutomatically = true)
	@Query("update VolumeWorkLoadMappingEntity vwme "
		+ "set vwme.deleteYN = 'Y'"
		+ "where vwme.workload.id =:jobId")
	void deleteByWorkloadId(@Param("jobId") Long jobId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("update VolumeWorkLoadMappingEntity vwm "
		+ "set vwm.deleteYN = 'Y' "
		+ "where vwm.volume.volumeId = :volumeId")
	void deleteByVolumeId(@Param("volumeId") Long volumeId);
}
