package com.xiilab.modulek8sdb.volume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.volume.entity.Volume;

public interface VolumeRepository extends JpaRepository<Volume, Long>, VolumeRepositoryCustom {
	@Query("UPDATE Volume v SET v.deleteYn = 'Y' WHERE v.volumeId = :volumeId")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteById(@Param("volumeId") Long volumeId);
}