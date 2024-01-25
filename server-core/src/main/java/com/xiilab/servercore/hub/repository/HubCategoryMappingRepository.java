package com.xiilab.servercore.hub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.hub.entity.HubCategoryMappingEntity;

@Repository
public interface HubCategoryMappingRepository extends JpaRepository<HubCategoryMappingEntity, Long> {
	@Query("SELECT hcm FROM TB_HUB_CATEGORY_MAPPING hcm "
		+ "join fetch hcm.hubCategoryEntity hc "
		+ "join fetch hcm.hubEntity h "
		+ "WHERE hc.name in :categoryNames "
		+ "group by h.hubId")
	Page<HubCategoryMappingEntity> finByHubsByCategoryNames(@Param("categoryNames") List<String> categoryNames, Pageable pageable);
	@Query("SELECT hcm FROM TB_HUB_CATEGORY_MAPPING hcm "
		+ "join fetch hcm.hubCategoryEntity hc "
		+ "join fetch hcm.hubEntity h "
		+ "where hcm.hubEntity.hubId in :hubIds")
	List<HubCategoryMappingEntity> findHcmJoinFetchByHubIds(@Param("hubIds") List<Long> hubIds);

	@Query("SELECT hcm FROM TB_HUB_CATEGORY_MAPPING hcm "
		+ "join fetch hcm.hubCategoryEntity hc "
		+ "join fetch hcm.hubEntity h "
		+ "where h.hubId = :hubId")
	List<HubCategoryMappingEntity> findHubCategoryMappingJoinFetchByHubId(@Param("hubId") Long hubId);
}
