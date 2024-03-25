package com.xiilab.modulek8sdb.dataset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkSpaceMappingEntity;

public interface DatasetWorkspaceRepository extends JpaRepository<DatasetWorkSpaceMappingEntity, Long> {
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from DatasetWorkSpaceMappingEntity dwm where dwm.dataset.datasetId = :datasetId")
	void deleteByDatasetId(@Param("datasetId") Long datasetId);


	@Query("select dwm  from DatasetWorkSpaceMappingEntity dwm "
		+ " join fetch dwm.dataset d where dwm.workspaceResourceName = :workspaceResourceName and dwm.dataset.deleteYn = 'N' "
		+ "order by dwm.regDate DESC ")
	List<DatasetWorkSpaceMappingEntity> findByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName);

	@Query("select dwm "
		+ "from DatasetWorkSpaceMappingEntity dwm "
		+ "where dwm.dataset.datasetId = :datasetId "
		+ "and dwm.dataset.deleteYn = 'N'"
		+ "and dwm.workspaceResourceName like :workspaceResourceName")
	DatasetWorkSpaceMappingEntity findByWorkspaceResourceNameAndDatasetId(@Param("workspaceResourceName") String workspaceResourceName, @Param("datasetId")Long datasetId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete "
		+ "from DatasetWorkSpaceMappingEntity dwm "
		+ "where dwm.dataset.datasetId = :datasetId "
		+ "and dwm.workspaceResourceName like :workspaceResourceName")
	void deleteByDatasetIdAndWorkspaceResourceName(@Param("datasetId")Long datasetId, @Param("workspaceResourceName") String workspaceResourceName);
}
