package com.xiilab.modulek8sdb.experiment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.experiment.entity.PanelEntity;

@Repository
public interface PanelRepository extends JpaRepository<PanelEntity, Long> {
	@Query("select c from PanelEntity c where c.workspace = ?1 and c.regUser.regUserId = ?2")
	Page<PanelEntity> findByRegUser_RegUserId(String workspace, String regUserId, Pageable pageable);
}
