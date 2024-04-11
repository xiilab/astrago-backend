package com.xiilab.modulek8sdb.network.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.network.entity.NetworkEntity;

public interface NetworkRepository extends JpaRepository<NetworkEntity, Long>{
	NetworkEntity findTopBy(Sort sort);
}
