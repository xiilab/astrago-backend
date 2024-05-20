package com.xiilab.modulek8sdb.dataset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.dataset.entity.Dataset;

public interface DatasetRepository extends JpaRepository<Dataset, Long>, DatasetRepositoryCustom {
	@Query("UPDATE Dataset d SET d.deleteYn = 'Y' WHERE d.datasetId = :datasetId")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteById(@Param("datasetId") Long datasetId);


}
