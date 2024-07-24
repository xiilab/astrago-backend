package com.xiilab.modulek8sdb.volume.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.volume.entity.VolumeWorkSpaceMappingEntity;

public interface VolumeWorkspaceRepository extends JpaRepository<VolumeWorkSpaceMappingEntity, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from VolumeWorkSpaceMappingEntity vwm where vwm.volume.volumeId = :volumeId")
	void deleteByVolumeId(@Param("volumeId") Long volumeId);

	@Query("select vwm  from VolumeWorkSpaceMappingEntity vwm "
		+ " join fetch vwm.volume m where vwm.workspaceResourceName = :workspaceResourceName and vwm.volume.deleteYn = 'N' "
		+ "order by vwm.regDate DESC")
	List<VolumeWorkSpaceMappingEntity> findByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName);

	@Query("select mwm "
		+ "from VolumeWorkSpaceMappingEntity mwm "
		+ "where mwm.volume.volumeId = :volumeId "
		+ "and mwm.volume.deleteYn = 'N'"
		+ "and mwm.workspaceResourceName like :workspaceResourceName")
	VolumeWorkSpaceMappingEntity findByWorkspaceResourceNameAndVolumeId(@Param("workspaceResourceName") String workspaceResourceName, @Param("volumeId") Long volumeId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete "
		+ "from VolumeWorkSpaceMappingEntity vwm "
		+ "where vwm.volume.volumeId = :volumeId "
		+ "and vwm.workspaceResourceName like :workspaceResourceName")
	void deleteByVolumeIdAndWorkspaceResourceName(@Param("volumeId") Long modelId, @Param("volumeId") String workspaceResourceName);
}
