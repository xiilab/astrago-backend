package com.xiilab.modulek8sdb.hub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.hub.entity.HubEntity;

public interface HubRepositoryCustom {
	Page<HubEntity> findHubs(String searchText, Pageable pageable);
}
