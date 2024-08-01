package com.xiilab.servercore.experiment.chart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.experiment.chart.entity.PanelEntity;

@Repository
public interface PanelRepository extends JpaRepository<PanelEntity, Long> {
	@Query("select c from PanelEntity c where c.regUser.regUserId = ?1")
	Page<PanelEntity> findByRegUser_RegUserId(String regUserId, Pageable pageable);
}
