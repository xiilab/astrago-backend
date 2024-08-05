package com.xiilab.modulek8sdb.mig.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.mig.entity.NodeEntity;

@Repository
public interface MigRepository extends JpaRepository<NodeEntity, Long> {

	Optional<NodeEntity> getByNodeName(String nodeName);
}
