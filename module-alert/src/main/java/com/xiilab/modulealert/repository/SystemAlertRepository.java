package com.xiilab.modulealert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.SystemAlertEntity;

@Repository
public interface SystemAlertRepository extends JpaRepository<SystemAlertEntity, Long> {
	// 수신받은 Alert List 조회
	List<SystemAlertEntity> getAlertEntitiesByRecipientId(String recipientId);

}
