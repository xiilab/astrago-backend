package com.xiilab.modulealert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulealert.entity.AlertEntity;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
	// 수신받은 Alert List 조회
	List<AlertEntity> getAlertEntitiesByRecipientId(String recipientId);

}
