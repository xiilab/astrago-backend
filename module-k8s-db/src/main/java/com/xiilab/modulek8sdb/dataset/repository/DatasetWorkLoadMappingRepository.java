package com.xiilab.modulek8sdb.dataset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;

public interface DatasetWorkLoadMappingRepository extends JpaRepository<DatasetWorkLoadMappingEntity, Long>{
}
