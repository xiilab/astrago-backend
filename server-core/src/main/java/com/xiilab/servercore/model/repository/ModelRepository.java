package com.xiilab.servercore.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xiilab.servercore.model.entity.Model;

public interface ModelRepository extends JpaRepository<Model, Long>, ModelRepositoryCustom {
}
