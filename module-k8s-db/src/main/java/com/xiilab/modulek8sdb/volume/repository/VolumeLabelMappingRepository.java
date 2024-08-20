package com.xiilab.modulek8sdb.volume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.volume.entity.VolumeLabelMappingEntity;

@Repository
public interface VolumeLabelMappingRepository extends JpaRepository<VolumeLabelMappingEntity, Long> {
	@Modifying
	@Query("delete from VolumeLabelMappingEntity v where v.id = ?1")
	void deleteByVolumeId(Long id);
}
