package com.xiilab.modulek8sdb.alert.systemalert.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;

@Repository
public interface AdminAlertMappingRepository extends JpaRepository<AdminAlertMappingEntity, Long> {
	@Query("select t from TB_ADMIN_ALERT_MAPPING t where t.alert.alertId = ?1")
	List<AdminAlertMappingEntity> findByAlert_AlertId(Long alertId);
	@Query("select t from TB_ADMIN_ALERT_MAPPING t where t.adminId = ?1 and t.alert.alertId = ?2")
	Optional<AdminAlertMappingEntity> findByAdminIdAndAlert_AlertId(String adminId, Long alertId);
}