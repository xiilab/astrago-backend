package com.xiilab.servercore.hub.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.hub.entity.HubEntity;
@Repository
public interface HubRepository extends JpaRepository<HubEntity, Long> {
	List<HubEntity> findByHubIdBetween(Long hubIdStart, Long hubIdEnd);
	HubEntity findByRegDateBetween(LocalDateTime regDateStart, LocalDateTime regDateEnd);
	HubEntity findByRegUser_RegUserIdLikeIgnoreCase(String regUserId);
}
