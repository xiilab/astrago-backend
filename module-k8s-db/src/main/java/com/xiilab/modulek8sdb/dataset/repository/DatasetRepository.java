package com.xiilab.modulek8sdb.dataset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.dataset.entity.Dataset;

public interface DatasetRepository extends JpaRepository<Dataset, Long>, DatasetRepositoryCustom {
}
