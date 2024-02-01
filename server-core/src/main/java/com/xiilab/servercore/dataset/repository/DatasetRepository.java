package com.xiilab.servercore.dataset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.servercore.dataset.entity.Dataset;

public interface DatasetRepository extends JpaRepository<Dataset, Long>, DatasetRepositoryCustom {
}
