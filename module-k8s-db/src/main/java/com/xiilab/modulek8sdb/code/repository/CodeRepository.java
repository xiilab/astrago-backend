package com.xiilab.modulek8sdb.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;

@Repository
public interface CodeRepository  extends JpaRepository<CodeEntity, Long> {
	List<CodeEntity> getAlertEntitiesByWorkspaceResourceNameAndRepositoryType(String workspaceResourceName, RepositoryType repositoryType);
}
