package com.xiilab.modulealert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.AlertSetEntity;

@Repository
public interface AlertSetRepository extends JpaRepository<AlertSetEntity, Long> {
	AlertSetEntity getAlertSetEntityByWorkspaceName(String workspaceName);
}
