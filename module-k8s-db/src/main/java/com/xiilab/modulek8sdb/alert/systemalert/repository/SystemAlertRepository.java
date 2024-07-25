package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;

public interface SystemAlertRepository extends JpaRepository<SystemAlertEntity, Long>, SystemAlertRepositoryCustom {
	List<SystemAlertEntity> getAlertEntitiesByRecipientId(String recipientId);

	@Query("select t from TB_SYSTEM_ALERT t where t.recipientId = :userId and t.readYN = 'N'")
	List<SystemAlertEntity> getSystemAlertEntitiesByRegUser_RegUserId(@Param("userId") String userId);
}
