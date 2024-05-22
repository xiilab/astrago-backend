package com.xiilab.modulek8sdb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.model.entity.Model;

public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {
	@Query("UPDATE Model m SET m.deleteYn = 'Y' WHERE m.modelId = :modelId")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteById(@Param("modelId") Long modelId);
}
