package com.xiilab.servercore.dataset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.DatasetWorkSpaceMappingEntity;

public interface DatasetWorkspaceRepository extends JpaRepository<DatasetWorkSpaceMappingEntity, Long> {
	@Modifying
	@Query("delete from DatasetWorkSpaceMappingEntity dwm where dwm.dataset.datasetId = :datasetId")
	void deleteByDatasetId(@Param("datasetId") Long datasetId);


	@Query("select dwm  from DatasetWorkSpaceMappingEntity dwm "
		+ " join fetch dwm.dataset d where dwm.workspaceResourceName = :workspaceResourceName")
	List<DatasetWorkSpaceMappingEntity> findByWorkspaceResourceName(@Param("workspaceResourceName") String workspaceResourceName);

	@Query("select dwm "
		+ "from DatasetWorkSpaceMappingEntity dwm "
		+ "where dwm.dataset.datasetId = :datasetId "
		+ "and dwm.workspaceResourceName like :workspaceResourceName")
	DatasetWorkSpaceMappingEntity findByWorkspaceResourceNameAndDatasetId(@Param("workspaceResourceName") String workspaceResourceName, @Param("datasetId")Long datasetId);
}
