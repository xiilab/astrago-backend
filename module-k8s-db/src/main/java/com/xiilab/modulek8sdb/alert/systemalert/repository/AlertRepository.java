package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;

public interface AlertRepository extends JpaRepository<AlertEntity, Long>, AlertRepositoryCustom {
	@Query("select t from TB_ALERT t where t.alertName = :alertName")
	Optional<AlertEntity> findByAlertName(String alertName);

	@Query("select t from TB_ALERT t where t.alertRole = ?1")
	List<AlertEntity> findByAlertRole(AlertRole alertRole);

	// TODO fetch 조인 안되는 현상 확인 필요함
	@Query("SELECT ta FROM TB_ALERT ta " +
		"LEFT JOIN ta.adminAlertMappingEntities taam " +
		"ON ta.alertId = taam.alert.alertId " +
		"AND taam.adminId = :adminId " +
		"WHERE ta.alertRole = :alertRole ")
	// "WHERE ta.alertRole = 'ADMIN'")
	List<AlertEntity> findAdminAlertMappingsByAdminId(@Param("adminId") String adminId, @Param("alertRole") AlertRole alertRole);

	@Query("select t from TB_ALERT t "
		+ "where t.alertName = :alertName "
		+ "and t.alertRole = :alertRole")
	Optional<AlertEntity> findByAlertNameAndAlertRole(@Param("alertName") String alertName, @Param("alertRole") AlertRole alertRole);
}
