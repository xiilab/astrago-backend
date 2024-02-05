package com.xiilab.servercore.dataset.repository;

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
}
