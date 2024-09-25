package com.xiilab.modulek8sdb.storage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xiilab.modulek8sdb.common.enums.DefaultStorageYN;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface StorageRepository extends JpaRepository<StorageEntity, Long> {
	@Query("select s from StorageEntity s where s.defaultStorageYN = ?1")
	Optional<StorageEntity> findByDefaultStorageYN(DefaultStorageYN defaultStorageYN);
	@Query("select s from StorageEntity s where s.deleteYN = ?1")
	Page<StorageEntity> findByDeleteYN(DeleteYN deleteYN, Pageable pageable);

	@Query("select s from StorageEntity s where s.deleteYN = 'N'")
	List<StorageEntity> findAll();

	@Query("select s from StorageEntity s where s.defaultStorageYN = 'Y'")
	StorageEntity getDefaultStorage();

	@Query("select v.storageEntity from AstragoVolumeEntity v where v.volumeId = ?1")
	StorageEntity getStorageClassByVolumeId(long volumeId);

}
