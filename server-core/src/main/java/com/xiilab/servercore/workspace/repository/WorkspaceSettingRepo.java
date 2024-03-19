package com.xiilab.servercore.workspace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.servercore.workspace.entity.WorkspaceSettingEntity;
@Repository
public interface WorkspaceSettingRepo extends JpaRepository<WorkspaceSettingEntity, Long> {
}
