package com.xiilab.servercore.code.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.code.entity.CodeEntity;

@Repository
public interface CodeRepository  extends JpaRepository<CodeEntity, Long> {
	List<CodeEntity> getAlertEntitiesByWorkspaceResourceName(String workspaceResourceName);
}
