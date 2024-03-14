package com.xiilab.modulek8sdb.hub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;

@Repository
public interface HubCategoryMappingRepository extends JpaRepository<HubCategoryMappingEntity, Long>, HubCategoryMappingRepositoryCustom {
	@Query("SELECT hcm FROM TB_HUB_CATEGORY_MAPPING hcm "
		+ "join fetch hcm.hubCategoryEntity hc "
		+ "join fetch hcm.hubEntity h "
		+ "where h.hubId = :hubId")
	List<HubCategoryMappingEntity> findHubCategoryMappingJoinFetchByHubId(@Param("hubId") Long hubId);
}
