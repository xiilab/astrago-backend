package com.xiilab.modulek8sdb.storage.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {
	@Query("select s from StorageEntity s where s.deleteYN = ?1")
	Page<StorageEntity> findByDeleteYN(DeleteYN deleteYN, Pageable pageable);

	@Query("select s from StorageEntity s where s.deleteYN = 'N'")
	List<StorageEntity> findAll();


	@Query("select a.storageEntity  from AstragoDatasetEntity a where a.datasetId = ?1")
	StorageEntity getDatasetStorageClassName(long id);

	@Query("select a.storageEntity  from AstragoModelEntity a where a.modelId = ?1")
	StorageEntity getModelVolumeStorageClassName(long id);
}
