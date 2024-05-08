package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;

public interface AlertRepository extends JpaRepository<AlertEntity, Long>, AlertRepositoryCustom {
	@Query("select t from TB_ALERT t where t.alertName = ?1")
	Optional<AlertEntity> findByAlertName(String alertName);

	@Query("select t from TB_ALERT t where t.alertRole = ?1")
	List<AlertEntity> findByAlertRole(AlertRole alertRole);

	@Query("select t from TB_ALERT t "
		+ "where t.alertName = :alertName "
		+ "and t.alertRole = :alertRole")
	Optional<AlertEntity> findByAlertNameAndAlertRole(@Param("alertName") String alertName,
		@Param("alertRole") AlertRole alertRole);
}
