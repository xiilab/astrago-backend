package com.xiilab.modulek8sdb.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;

public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {
	@Query("UPDATE Model m SET m.deleteYn = 'Y' WHERE m.modelId = :modelId")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteById(@Param("modelId") Long modelId);
	@Query("SELECT m FROM AstragoModelEntity m where m.deleteYn = 'N' and m.storageEntity =:storageEntity")
	List<Model> findByStorageId(@Param("storageEntity") StorageEntity storageEntity);
}
