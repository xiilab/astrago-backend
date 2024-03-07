package com.xiilab.modulek8sdb.hub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;

@Repository
public interface HubCategoryMappingRepositoryCustom {
	Page<HubCategoryMappingEntity> findHubs(List<String> categoryNames, List<Long> hubIds, Pageable pageable);
}
