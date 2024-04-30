package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulecommon.alert.enums.AlertRole;

public interface AlertRepository extends JpaRepository<AlertEntity, Long>, AlertRepositoryCustom {
	@Query("select t from TB_ALERT t where t.alertName = ?1")
	Optional<AlertEntity> findByAlertName(String alertName);

	@Query("select t from TB_ALERT t where t.alertRole = ?1")
	List<AlertEntity> findByAlertRole(AlertRole alertRole);

	@Query("SELECT ta FROM TB_ALERT ta " +
		"LEFT JOIN ta.adminAlertMappingEntities taam " +
		"WHERE ta.alertRole = :alertRole AND taam.adminId = :adminId")
	List<AlertEntity> findAdminAlertMappingsByAdminId(@Param("adminId") String adminId, @Param("alertRole") AlertRole alertRole);
	// "ON ta.alertId = taam.alert.alertId " +
	// "ON taam.adminId = :adminId " +
	@Query("select t from TB_ALERT t "
		+ "where t.alertName = :alertName "
		+ "and t.alertRole = :alertRole")
	Optional<AlertEntity> findByAlertNameAndAlertRole(@Param("alertName") String alertName, @Param("alertRole") AlertRole alertRole);
}
