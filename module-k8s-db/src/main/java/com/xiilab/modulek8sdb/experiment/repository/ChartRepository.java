package com.xiilab.modulek8sdb.experiment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.experiment.entity.ChartEntity;

@Repository
public interface ChartRepository extends JpaRepository<ChartEntity, Long> {
	@Query("select c from ChartEntity c where c.panel.id = ?1")
	Page<ChartEntity> findByChartPart_Id(Long id, Pageable pageable);
}
