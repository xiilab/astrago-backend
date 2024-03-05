package com.xiilab.modulealert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.SystemAlertSetEntity;

@Repository
public interface SystemAlertSetRepository extends JpaRepository<SystemAlertSetEntity, Long> {
	SystemAlertSetEntity getAlertSetEntityByWorkspaceName(String workspaceName);
}
