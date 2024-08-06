package com.xiilab.modulek8sdb.mig.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.mig.entity.MigInfoEntity;

@Repository
public interface MigRepository extends JpaRepository<MigInfoEntity, Long> {

	List<MigInfoEntity> getAllByNodeName(String nodeName);
}
