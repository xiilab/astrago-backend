package com.xiilab.modulek8sdb.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.modulek8sdb.model.entity.Model;

public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {
}
